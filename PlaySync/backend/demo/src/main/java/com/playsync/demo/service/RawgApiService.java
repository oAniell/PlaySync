package com.playsync.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.playsync.demo.Entities.GenerosApiRawg;
import com.playsync.demo.Entities.LojasRawgApi;
import com.playsync.demo.Entities.PlataformasRawg;
import com.playsync.demo.Entities.RawgApiBuscaTermo;
import com.playsync.demo.Entities.TotalItensBuscadosRawg;
import com.playsync.demo.client.RawgClient;

import com.playsync.demo.dtoresponse.GenerosApiRawgDTO;
import com.playsync.demo.dtoresponse.LojasRawgApiDTO;
import com.playsync.demo.dtoresponse.PlataformasRawgDTO;
import com.playsync.demo.dtoresponse.PlataformsRawg;
import com.playsync.demo.dtoresponse.RawgApiBuscaTermoDTO;
import com.playsync.demo.dtoresponse.StoresRawg;
import com.playsync.demo.dtoresponse.TotalItensBuscadosRawgDTO;
import com.playsync.demo.repository.GenerosApiRawgRepository;
import com.playsync.demo.repository.LojasRawgApiRepository;
import com.playsync.demo.repository.PlataformasRawgRepository;
import com.playsync.demo.repository.RawgApiBuscaTermoRepository;
import com.playsync.demo.repository.TotalItensBuscadosRawgRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RawgApiService {

	private final RawgClient rawgClient;
	private final TotalItensBuscadosRawgRepository totalItensBuscadosRawgRepository;
	private final RawgApiBuscaTermoRepository rawgApiBuscaTermoRepository;
	private final PlataformasRawgRepository plataformasRawgRepository;
	private final GenerosApiRawgRepository generosApiRawgRepository;
	private final LojasRawgApiRepository lojasRawgApiRepository;

	public void principalMethod(String nomeJogo) {
		if (temItemAtrasado(nomeJogo)) {
		}

	}

	public TotalItensBuscadosRawgDTO buscarItensNaApi(String nomeJogo) {
		TotalItensBuscadosRawgDTO totalItensBuscadosRawgDTO = this.rawgClient.buscarPorTermoRawg(nomeJogo).block();

		if (totalItensBuscadosRawgDTO == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, " Nao foi encontrado itens por esse termo");
		}
		if (temItemAtrasado(nomeJogo)) {

		}

		persisteInformacaoNoBanco(totalItensBuscadosRawgDTO);
		return totalItensBuscadosRawgDTO;
	}

	private Boolean temItemAtrasado(String termo) {
		List<RawgApiBuscaTermo> rawgApiBuscaTermo = this.rawgApiBuscaTermoRepository.selectByName(termo);
		List<RawgApiBuscaTermo> itensQuePassouDoTempoPrazo = new ArrayList<>();
		for (RawgApiBuscaTermo rawgApiBuscaTermoList : rawgApiBuscaTermo) {
			LocalDateTime dataLimite = LocalDateTime.now().minusSeconds(10);
			if (rawgApiBuscaTermoList.getDataLastSearch().isBefore(dataLimite)) {
				itensQuePassouDoTempoPrazo.add(rawgApiBuscaTermoList);
			}
		}
		if (!itensQuePassouDoTempoPrazo.isEmpty()) {
			atualizaInformacaoNoBanco(itensQuePassouDoTempoPrazo, termo);
			return true;
		}
		return false;

	}

	@Transactional
	private void atualizaInformacaoNoBanco(List<RawgApiBuscaTermo> itensQuePassouDoTempoPrazo,
			List<RawgApiBuscaTermo> listaCompleta, String termo) {
		TotalItensBuscadosRawgDTO totalItensBuscadosRawgDTO = this.rawgClient.buscarPorTermoRawg(termo).block();
		for (RawgApiBuscaTermoDTO rawgApiBuscaTermoDTO : totalItensBuscadosRawgDTO.getRawgApiBuscaTermo()) {
			for (RawgApiBuscaTermo rawgApiBuscaTermo : itensQuePassouDoTempoPrazo) {
				if (rawgApiBuscaTermoDTO.getIdGame().equals(rawgApiBuscaTermo.getIdGame())) {
					rawgApiBuscaTermo.setDataLastSearch(LocalDateTime.now());
					rawgApiBuscaTermo.setImgBackground(rawgApiBuscaTermoDTO.getImgBackground());
					rawgApiBuscaTermo.setNome(rawgApiBuscaTermoDTO.getNome());
					rawgApiBuscaTermo.setNotaMediaJogo(rawgApiBuscaTermoDTO.getNotaMediaJogo());
					rawgApiBuscaTermo.setNumeroAvaliacoes(rawgApiBuscaTermoDTO.getNumeroAvaliacoes());
					rawgApiBuscaTermo.setDataLancamento(rawgApiBuscaTermoDTO.getDataLancamento());
					if (rawgApiBuscaTermo.getPlataformasRawgs() != null
							&& !rawgApiBuscaTermo.getPlataformasRawgs().isEmpty()) {
						for (PlataformasRawg plataformasRawg : rawgApiBuscaTermo.getPlataformasRawgs()) {
							for (PlataformsRawg plataformasRawgDTO : rawgApiBuscaTermoDTO.getPlataformas()) {
								if (plataformasRawg.getId()
										.equals(plataformasRawgDTO.getPlataformasRawgDTO().getIdPlataforma())) {
									plataformasRawg
											.setPlataforma(plataformasRawgDTO.getPlataformasRawgDTO().getPlataforma());
								}
							}
						}

					}
					if (rawgApiBuscaTermo.getGenerosApiRawgs() != null
							&& !rawgApiBuscaTermo.getGenerosApiRawgs().isEmpty()) {
						for (GenerosApiRawg generosApiRawg : rawgApiBuscaTermo.getGenerosApiRawgs()) {
							for (GenerosApiRawgDTO generosApiRawgDTO : rawgApiBuscaTermoDTO.getGenerosApiRawgDTOs()) {
								if (generosApiRawg.getId().equals(generosApiRawgDTO.getIdGeneros())) {
									generosApiRawg.setNome(generosApiRawgDTO.getNome());
								}
							}
						}
					}
					if (rawgApiBuscaTermo.getLojasRawgApis() != null
							&& !rawgApiBuscaTermo.getLojasRawgApis().isEmpty()) {
						for (LojasRawgApi lojasRawgApi : rawgApiBuscaTermo.getLojasRawgApis()) {
							for (StoresRawg storesRawg : rawgApiBuscaTermoDTO.getStoresRawgs()) {
								if (lojasRawgApi.getIdLoja().equals(storesRawg.getLojasRawgApiDTO().getIdLoja())) {
									lojasRawgApi.setNome(storesRawg.getLojasRawgApiDTO().getNome());
								}
							}
						}
					}
				}
			}
		}
		this.rawgApiBuscaTermoRepository.saveAll(itensQuePassouDoTempoPrazo);
	}

	private void persisteInformacaoNoBanco(TotalItensBuscadosRawgDTO totalItensBuscadosRawgDTO) {
		TotalItensBuscadosRawg totalItensBuscadosRawg = new TotalItensBuscadosRawg(
				totalItensBuscadosRawgDTO.getQuantidadeDeItens());
		List<RawgApiBuscaTermo> rawgApiBuscaTermos = new ArrayList<>();
		List<PlataformasRawg> plataformasRawgs = new ArrayList<>();
		List<GenerosApiRawg> generosApiRawgs = new ArrayList<>();
		List<LojasRawgApi> lojasRawgApis = new ArrayList<>();
		for (RawgApiBuscaTermoDTO i : totalItensBuscadosRawgDTO.getRawgApiBuscaTermo()) {
			RawgApiBuscaTermo rawgApiBuscaTermo = new RawgApiBuscaTermo(i.getNome(), i.getDataLancamento(),
					i.getImgBackground(),
					i.getNotaMediaJogo(), i.getNumeroAvaliacoes(), totalItensBuscadosRawg, LocalDateTime.now());
			rawgApiBuscaTermos.add(rawgApiBuscaTermo);
			validacaoStores(i, rawgApiBuscaTermo, lojasRawgApis);
			validacaoGenres(i, rawgApiBuscaTermo, generosApiRawgs);
			validacaoPlataforms(i, rawgApiBuscaTermo, plataformasRawgs);

		}
		this.totalItensBuscadosRawgRepository.save(totalItensBuscadosRawg);
		this.rawgApiBuscaTermoRepository.saveAll(rawgApiBuscaTermos);
		this.plataformasRawgRepository.saveAll(plataformasRawgs);
		this.generosApiRawgRepository.saveAll(generosApiRawgs);
		this.lojasRawgApiRepository.saveAll(lojasRawgApis);
	}

	private void validacaoStores(RawgApiBuscaTermoDTO rawgApiBuscaTermoDTO, RawgApiBuscaTermo rawgApiBuscaTermo,
			List<LojasRawgApi> lojasRawgApis) {

		if (rawgApiBuscaTermoDTO.getStoresRawgs() != null && !rawgApiBuscaTermoDTO.getStoresRawgs().isEmpty()) {
			for (StoresRawg storesRawg : rawgApiBuscaTermoDTO.getStoresRawgs()) {
				if (storesRawg != null && storesRawg.getLojasRawgApiDTO() != null
						&& storesRawg.getLojasRawgApiDTO().getNome() != null) {
					LojasRawgApi lojasRawgApi = new LojasRawgApi(storesRawg.getLojasRawgApiDTO().getNome(),
							storesRawg.getLojasRawgApiDTO().getIdLoja(),
							rawgApiBuscaTermo);
					lojasRawgApis.add(lojasRawgApi);
				}
			}

		}
	}

	private void validacaoGenres(RawgApiBuscaTermoDTO rawgApiBuscaTermoDTO, RawgApiBuscaTermo rawgApiBuscaTermo,
			List<GenerosApiRawg> generosApiRawgs) {
		if (rawgApiBuscaTermoDTO.getGenerosApiRawgDTOs() != null
				&& !rawgApiBuscaTermoDTO.getGenerosApiRawgDTOs().isEmpty()) {
			for (GenerosApiRawgDTO generosApiRawgDTO : rawgApiBuscaTermoDTO.getGenerosApiRawgDTOs()) {
				if (generosApiRawgDTO != null && generosApiRawgDTO.getNome() != null) {
					GenerosApiRawg generosApiRawgEntity = new GenerosApiRawg(generosApiRawgDTO.getNome(),
							generosApiRawgDTO.getIdGeneros(),
							rawgApiBuscaTermo);
					generosApiRawgs.add(generosApiRawgEntity);
				}
			}
		}
	}

	private void validacaoPlataforms(RawgApiBuscaTermoDTO rawgApiBuscaTermoDTO, RawgApiBuscaTermo rawgApiBuscaTermo,
			List<PlataformasRawg> plataformasRawgs) {
		if (rawgApiBuscaTermoDTO.getPlataformas() != null && !rawgApiBuscaTermoDTO.getPlataformas().isEmpty()) {
			for (PlataformsRawg plataformsRawg : rawgApiBuscaTermoDTO.getPlataformas()) {
				if (plataformsRawg != null && plataformsRawg.getPlataformasRawgDTO() != null
						&& plataformsRawg.getPlataformasRawgDTO().getPlataforma() != null) {
					PlataformasRawg plataformasRawg = new PlataformasRawg(
							plataformsRawg.getPlataformasRawgDTO().getPlataforma(),
							plataformsRawg.getPlataformasRawgDTO().getIdPlataforma(), rawgApiBuscaTermo);
					plataformasRawgs.add(plataformasRawg);
				}
			}
		}
	}

	private TotalItensBuscadosRawg montaDto(List<RawgApiBuscaTermo> lista) {
		TotalItensBuscadosRawg totalItensBuscadosRawg = new TotalItensBuscadosRawg(lista.size());
		List<RawgApiBuscaTermoDTO> rawgApiBuscaTermoDTOs = new ArrayList<>();
		for (RawgApiBuscaTermo listaEntity : lista) {
			RawgApiBuscaTermoDTO rawgApiBuscaTermoDTO = new RawgApiBuscaTermoDTO(listaEntity.getNome(),
					listaEntity.getDataLancamento(),
					listaEntity.getImgBackground(), listaEntity.getIdGame(), listaEntity.getNotaMediaJogo(),
					listaEntity.getNumeroAvaliacoes(), null, null, null);
			rawgApiBuscaTermoDTOs.add(rawgApiBuscaTermoDTO);
			for (PlataformasRawg plataformasRawg : listaEntity.getPlataformasRawgs()) {
				PlataformsRawg plataformsRawg = new PlataformsRawg(
						new PlataformasRawgDTO(plataformasRawg.getIdPlataforma(), plataformasRawg.getPlataforma()));
			}
			

		}
	}

}
