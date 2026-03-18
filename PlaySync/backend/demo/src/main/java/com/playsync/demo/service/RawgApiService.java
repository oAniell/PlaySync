package com.playsync.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.playsync.demo.Entities.GenerosApiRawg;
import com.playsync.demo.Entities.LojasRawgApi;
import com.playsync.demo.Entities.PlataformasRawgEntity;

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

	public TotalItensBuscadosRawgDTO principalMethod(String nomeJogo) {
		List<RawgApiBuscaTermo> rawgApiBuscaTermos = this.rawgApiBuscaTermoRepository.selectByName(nomeJogo);
		if (rawgApiBuscaTermos == null || rawgApiBuscaTermos.isEmpty()) {
			return buscarItensNaApiRawg(nomeJogo);
		}
		return validaINformacaoVindoDoBanco(nomeJogo, rawgApiBuscaTermos);

	}

	private TotalItensBuscadosRawgDTO buscarItensNaApiRawg(String nomeJogo) {
		TotalItensBuscadosRawgDTO totalItensBuscadosRawgDTO = this.rawgClient.buscarPorTermoRawg(nomeJogo).block();
		return persisteInformacaoNoBanco(totalItensBuscadosRawgDTO);
	}

	public TotalItensBuscadosRawgDTO validaINformacaoVindoDoBanco(String nomeJogo,
			List<RawgApiBuscaTermo> rawgApiBuscaTermos) {
		List<RawgApiBuscaTermo> listaAtrasados = itensAtrasados(rawgApiBuscaTermos);
		if (listaAtrasados != null && !listaAtrasados.isEmpty()) {
			return atualizaInformacaoNoBancoERetornaDto(listaAtrasados, rawgApiBuscaTermos, nomeJogo);
		}
		return montaDto(rawgApiBuscaTermos);

	}

	private List<RawgApiBuscaTermo> itensAtrasados(List<RawgApiBuscaTermo> rawgApiBuscaTermo) {
		List<RawgApiBuscaTermo> listaAtrasados = new ArrayList<>();
		for (RawgApiBuscaTermo rawgApiBuscaTermoEntity : rawgApiBuscaTermo) {
			LocalDateTime dataLimite = LocalDateTime.now().minusSeconds(10);
			if (rawgApiBuscaTermoEntity.getDataLastSearch().isBefore(dataLimite)) {
				listaAtrasados.add(rawgApiBuscaTermoEntity);

			}
		}
		return listaAtrasados;

	}

	@Transactional
	private TotalItensBuscadosRawgDTO atualizaInformacaoNoBancoERetornaDto(
			List<RawgApiBuscaTermo> itensQuePassouDoTempoPrazo,
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
						for (PlataformasRawgEntity plataformasRawg : rawgApiBuscaTermo.getPlataformasRawgs()) {
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
		return montaDto(listaCompleta);
	}

	private TotalItensBuscadosRawgDTO persisteInformacaoNoBanco(TotalItensBuscadosRawgDTO totalItensBuscadosRawgDTO) {
		TotalItensBuscadosRawg totalItensBuscadosRawg = new TotalItensBuscadosRawg(
				totalItensBuscadosRawgDTO.getQuantidadeDeItens());
		List<RawgApiBuscaTermo> rawgApiBuscaTermos = new ArrayList<>();
		for (RawgApiBuscaTermoDTO i : totalItensBuscadosRawgDTO.getRawgApiBuscaTermo()) {
			RawgApiBuscaTermo rawgApiBuscaTermo = new RawgApiBuscaTermo(i.getNome(), i.getDataLancamento(),
					i.getImgBackground(),
					i.getNotaMediaJogo(), i.getNumeroAvaliacoes(), i.getIdGame(), totalItensBuscadosRawg,
					LocalDateTime.now());
			rawgApiBuscaTermos.add(rawgApiBuscaTermo);
			validacaoStores(i, rawgApiBuscaTermo);
			validacaoGenres(i, rawgApiBuscaTermo);
			validacaoPlataforms(i, rawgApiBuscaTermo);

		}
		this.totalItensBuscadosRawgRepository.save(totalItensBuscadosRawg);
		this.rawgApiBuscaTermoRepository.saveAll(rawgApiBuscaTermos);

		return montaDto(rawgApiBuscaTermos);
	}

	@Transactional
	private void validacaoStores(RawgApiBuscaTermoDTO rawgApiBuscaTermoDTO, RawgApiBuscaTermo rawgApiBuscaTermo) {

		if (rawgApiBuscaTermoDTO.getStoresRawgs() != null && !rawgApiBuscaTermoDTO.getStoresRawgs().isEmpty()) {
			for (StoresRawg storesRawg : rawgApiBuscaTermoDTO.getStoresRawgs()) {
				if (storesRawg != null && storesRawg.getLojasRawgApiDTO() != null
						&& storesRawg.getLojasRawgApiDTO().getNome() != null) {
					LojasRawgApi lojasRawgApi = new LojasRawgApi(storesRawg.getLojasRawgApiDTO().getNome(),
							storesRawg.getLojasRawgApiDTO().getIdLoja());
					lojasRawgApi.setRawgApiBuscaTermo(rawgApiBuscaTermo);
					rawgApiBuscaTermo.getLojasRawgApis().add(lojasRawgApi);
				}
			}

		}

	}

	@Transactional
	private void validacaoGenres(RawgApiBuscaTermoDTO rawgApiBuscaTermoDTO, RawgApiBuscaTermo rawgApiBuscaTermo) {
		if (rawgApiBuscaTermoDTO.getGenerosApiRawgDTOs() != null
				&& !rawgApiBuscaTermoDTO.getGenerosApiRawgDTOs().isEmpty()) {
			for (GenerosApiRawgDTO generosApiRawgDTO : rawgApiBuscaTermoDTO.getGenerosApiRawgDTOs()) {
				if (generosApiRawgDTO != null && generosApiRawgDTO.getNome() != null) {
					GenerosApiRawg generosApiRawgEntity = new GenerosApiRawg(generosApiRawgDTO.getNome(),
							generosApiRawgDTO.getIdGeneros());
					generosApiRawgEntity.setRawgApiBuscaTermo(rawgApiBuscaTermo);
					rawgApiBuscaTermo.getGenerosApiRawgs().add(generosApiRawgEntity);
				}
			}
		}
	}

	@Transactional
	private void validacaoPlataforms(RawgApiBuscaTermoDTO rawgApiBuscaTermoDTO, RawgApiBuscaTermo rawgApiBuscaTermo) {
		if (rawgApiBuscaTermoDTO.getPlataformas() != null && !rawgApiBuscaTermoDTO.getPlataformas().isEmpty()) {
			for (PlataformsRawg plataformsRawg : rawgApiBuscaTermoDTO.getPlataformas()) {
				if (plataformsRawg != null && plataformsRawg.getPlataformasRawgDTO() != null
						&& plataformsRawg.getPlataformasRawgDTO().getPlataforma() != null) {
					PlataformasRawgEntity plataformasRawg = new PlataformasRawgEntity(
							plataformsRawg.getPlataformasRawgDTO().getPlataforma(),
							plataformsRawg.getPlataformasRawgDTO().getIdPlataforma());
					plataformasRawg.setRawgApiBuscaTermo(rawgApiBuscaTermo);
					rawgApiBuscaTermo.getPlataformasRawgs().add(plataformasRawg);
				}
			}
		}
	}

	private TotalItensBuscadosRawgDTO montaDto(List<RawgApiBuscaTermo> lista) {
		TotalItensBuscadosRawgDTO totalItensBuscadosRawg = new TotalItensBuscadosRawgDTO(lista.size(), null);
		List<RawgApiBuscaTermoDTO> rawgApiBuscaTermoDTOs = new ArrayList<>();
		for (RawgApiBuscaTermo listaEntity : lista) {
			List<PlataformsRawg> plataformsRawgsList = new ArrayList<>();
			List<GenerosApiRawgDTO> generosApiRawgDTOsList = new ArrayList<>();
			List<StoresRawg> storesRawgsList = new ArrayList<>();
			RawgApiBuscaTermoDTO rawgApiBuscaTermoDTO = new RawgApiBuscaTermoDTO(listaEntity.getNome(),
					listaEntity.getDataLancamento(),
					listaEntity.getImgBackground(), listaEntity.getIdGame(), listaEntity.getNotaMediaJogo(),
					listaEntity.getNumeroAvaliacoes(), null, null, null);
			rawgApiBuscaTermoDTOs.add(rawgApiBuscaTermoDTO);
			for (PlataformasRawgEntity plataformasRawg : listaEntity.getPlataformasRawgs()) {
				PlataformsRawg plataformsRawg = new PlataformsRawg(
						new PlataformasRawgDTO(plataformasRawg.getIdPlataforma(), plataformasRawg.getPlataforma()));
				plataformsRawgsList.add(plataformsRawg);

			}
			for (GenerosApiRawg generosApiRawg : listaEntity.getGenerosApiRawgs()) {
				GenerosApiRawgDTO generosApiRawgDTO = new GenerosApiRawgDTO(generosApiRawg.getIdGenero(),
						generosApiRawg.getNome());
				generosApiRawgDTOsList.add(generosApiRawgDTO);
			}
			for (LojasRawgApi lojasRawgApi : listaEntity.getLojasRawgApis()) {
				StoresRawg storesRawg = new StoresRawg(
						new LojasRawgApiDTO(lojasRawgApi.getIdLoja(), lojasRawgApi.getNome()));
				storesRawgsList.add(storesRawg);
			}
			rawgApiBuscaTermoDTO.setPlataformas(plataformsRawgsList);
			rawgApiBuscaTermoDTO.setGenerosApiRawgDTOs(generosApiRawgDTOsList);
			rawgApiBuscaTermoDTO.setStoresRawgs(storesRawgsList);

		}

		totalItensBuscadosRawg.setRawgApiBuscaTermo(rawgApiBuscaTermoDTOs);
		return totalItensBuscadosRawg;

	}

}
