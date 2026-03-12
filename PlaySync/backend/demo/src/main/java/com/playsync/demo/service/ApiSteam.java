package com.playsync.demo.service;

import java.time.LocalDateTime;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.playsync.demo.Entities.BuscaPorTermo;
import com.playsync.demo.Entities.ItensBuscadorPeloTermo;
import com.playsync.demo.Entities.PrecosJogos;
import com.playsync.demo.client.SteamClient;
import com.playsync.demo.dtoresponse.BuscaPorTermoDTO;
import com.playsync.demo.dtoresponse.ItensFiltradosPeloTermoDTO;
import com.playsync.demo.dtoresponse.PrecoDeItensDTO;
import com.playsync.demo.repository.BuscaPorTermoRepository;
import com.playsync.demo.repository.ItensBuscadosPeloTermoRepository;
import com.playsync.demo.repository.PrecoPorJogoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ApiSteam {

	private final SteamClient webConfig;
	private final ItensBuscadosPeloTermoRepository itensRepository;
	private final BuscaPorTermoRepository buscaPorTermoRepository;
	private final PrecoPorJogoRepository precoRepository;

	@Transactional
	public BuscaPorTermoDTO buscaPorTermo(String termo) {

		List<ItensBuscadorPeloTermo> itensNoBanco = this.itensRepository.findByName(termo);
		System.out.println("Itens no banco: " + itensNoBanco);
		List<ItensFiltradosPeloTermoDTO> itensVindoDaApi = new ArrayList<>();
		
		System.out.println("Validacao: " + validacao(itensNoBanco));
		if (itensNoBanco.isEmpty()) {
			System.out.println("Banco vazio, chamando API externa...");
			return metodoChamaApiEPersiste(termo);
		}
		if (validacao(itensNoBanco)) {
			return atualiza(itensNoBanco, termo);
		}
		return formataEmDto(itensNoBanco);

	}

	private BuscaPorTermoDTO metodoChamaApiEPersiste(String termo) {
		System.out.println("Chamando API externa para termo: " + termo);
		BuscaPorTermoDTO buscaDto = this.webConfig.buscarPorTermo(termo).block();
		
		if (buscaDto == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nao encontrado nenhuma informacao");
		}
		
		System.out.println("Resposta da API: " + buscaDto);

		List<ItensBuscadorPeloTermo> listaDeItens = new ArrayList<>();
		BuscaPorTermo buscaPorTermo = new BuscaPorTermo(buscaDto.getItens().size());
		for (ItensFiltradosPeloTermoDTO itens : buscaDto.getItens()) {
			itens.getPrecos().setPrecoFinal(itens.getPrecos().getPrecoFinal()/100.0);
			itens.getPrecos().setPrecoInicial((itens.getPrecos().getPrecoInicial()/100.0));
			System.out.println(itens.getPrecos().getPrecoFinal());
			ItensBuscadorPeloTermo itensBuscadoPeloTermo = new ItensBuscadorPeloTermo(itens.getIdGame(),
					itens.getName(), buscaPorTermo, itens.getImg(), false, LocalDateTime.now());
			PrecoDeItensDTO precoDTO = itens.getPrecos();

			if (precoDTO != null) {

				PrecosJogos preco = new PrecosJogos(precoDTO.getPrecoInicial(), precoDTO.getPrecoFinal(),
						itensBuscadoPeloTermo);
				itensBuscadoPeloTermo.getPrecos().add(preco);
			}
			listaDeItens.add(itensBuscadoPeloTermo);

		}

		this.buscaPorTermoRepository.save(buscaPorTermo);
		this.itensRepository.saveAll(listaDeItens);
		return buscaDto;
	}

	private Boolean validacao(List<ItensBuscadorPeloTermo> lista) {
		LocalDateTime dataLimite = LocalDateTime.now().minusSeconds(10);
		for (ItensBuscadorPeloTermo i : lista) {
			if (i.getDataPesquisaUsuario().isBefore(dataLimite)) {
				return true;
			}
		}
		return false;
	}

	private BuscaPorTermoDTO atualiza(List<ItensBuscadorPeloTermo> lista, String termo) {
		LocalDateTime dataLimite = LocalDateTime.now().minusSeconds(10);
		BuscaPorTermoDTO respostaApi = webConfig.buscarPorTermo(termo).block();
		List<ItensBuscadorPeloTermo> listaDeItensVencidos = new ArrayList<>();

		for (ItensBuscadorPeloTermo i : lista) {
			if (i.getDataPesquisaUsuario().isBefore(dataLimite)) {
				listaDeItensVencidos.add(i);
			}
		}
		for (ItensFiltradosPeloTermoDTO i : respostaApi.getItens()) {
			for (ItensBuscadorPeloTermo it : listaDeItensVencidos) {
				if (i.getIdGame().equals(it.getIdGame())) {
					for (PrecosJogos p : it.getPrecos()) {
						if (p.getPreco().getId().equals(it.getId())) {
							p.setPrecoFinal(i.getPrecos().getPrecoFinal()/100.0);
							p.setPrecoInicial(i.getPrecos().getPrecoInicial()/100.0);
							this.precoRepository.save(p);
						}
					}
					it.setDataPesquisaUsuario(LocalDateTime.now());
				}

			}
		}

		this.itensRepository.saveAll(listaDeItensVencidos);
		List<ItensBuscadorPeloTermo> listaAtualizada = this.itensRepository.findByName(termo);
		return formataEmDto(listaAtualizada);
	}

	private BuscaPorTermoDTO formataEmDto(List<ItensBuscadorPeloTermo> lista) {

		List<ItensFiltradosPeloTermoDTO> itensDto = new ArrayList<>();

		for (ItensBuscadorPeloTermo i : lista) {

			if (i.getPrecos().isEmpty()) {

				itensDto.add(new ItensFiltradosPeloTermoDTO(i.getIdGame(), i.getNome(), new PrecoDeItensDTO(0.0, 0.0), i.getImg(), null));

			} else {

				PrecosJogos p = i.getPrecos().get(0);
				System.out.println("Item: " + i.getNome() + " - Preco Final: " + p.getPrecoFinal() + " - Preco Inicial: " + p.getPrecoInicial());
				itensDto.add(new ItensFiltradosPeloTermoDTO(i.getIdGame(), i.getNome(),
						new PrecoDeItensDTO(p.getPrecoInicial(), p.getPrecoFinal()), i.getImg(), null));
			}
		}
		BuscaPorTermoDTO result = new BuscaPorTermoDTO(itensDto.size(), itensDto);
		System.out.println("DTO retornado: " + result);
		System.out.println("Itens no DTO: " + result.getItens());
		return result;
	}

	
}
