package com.playsync.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.playsync.demo.Entities.ItadAssetsDeItens;
import com.playsync.demo.Entities.ItadBuscaPorTermo;
import com.playsync.demo.client.ItadClient;
import com.playsync.demo.dtoresponse.ItadAssetsLista;
import com.playsync.demo.dtoresponse.ItadBuscaPorTermoDto;
import com.playsync.demo.repository.ItadBuscaPorTermoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItadApiService {

    private final ItadClient itadClient;
    private final ItadBuscaPorTermoRepository itadBuscaPorTermoRepository;

    public List<ItadBuscaPorTermoDto> principalMethod(String termoString) {
        List<ItadBuscaPorTermo> buscaNoBanco = this.itadBuscaPorTermoRepository.findByNome(termoString);

        if (buscaNoBanco == null || buscaNoBanco.isEmpty()) {
            return salvaInformacaoNoBanco(buscaNaApi(termoString));
        }

        return validaInformacaoNoBanco(buscaNoBanco, termoString);
    }

    private List<ItadBuscaPorTermoDto> buscaNaApi(String termoString) {
        List<ItadBuscaPorTermoDto> response = this.itadClient.buscarPorTermo(termoString).block();
        return response != null ? response : new ArrayList<>();
    }

    private List<ItadBuscaPorTermoDto> salvaInformacaoNoBanco(List<ItadBuscaPorTermoDto> dtos) {

        List<String> ids = new ArrayList<>();
        for (ItadBuscaPorTermoDto dto : dtos) {
            ids.add(dto.getIdGame());
        }

        List<ItadBuscaPorTermo> existentes = this.itadBuscaPorTermoRepository.findByIdGameIn(ids);

        Map<String, ItadBuscaPorTermo> mapa = new HashMap<>();
        if (existentes != null) {
            for (ItadBuscaPorTermo e : existentes) {
                mapa.put(e.getIdGame(), e);
            }
        }

        List<ItadBuscaPorTermo> salvar = new ArrayList<>();

        for (ItadBuscaPorTermoDto dto : dtos) {

            if (mapa.containsKey(dto.getIdGame())) {
                continue;
            }

            ItadBuscaPorTermo entity = new ItadBuscaPorTermo(
                    dto.getIdGame(),
                    dto.getSlug(),
                    dto.getNomeJogo(),
                    dto.getTipoDoItem(),
                    LocalDateTime.now(),
                    null);

            ItadAssetsDeItens assets = null;

            if (dto.getItadAssetsLista() != null) {
                assets = new ItadAssetsDeItens(
                        dto.getItadAssetsLista().getImagem01(),
                        dto.getItadAssetsLista().getArteSecundaria(),
                        dto.getIdGame(),
                        null,
                        LocalDateTime.now());
            }

            if (assets != null) {
                assets.setItadBuscaPorTermo(entity);
                entity.setAssetsItens(assets);
            }

            salvar.add(entity);
        }

        if (!salvar.isEmpty()) {
            this.itadBuscaPorTermoRepository.saveAll(salvar);
        }

        List<ItadBuscaPorTermo> retorno = this.itadBuscaPorTermoRepository.findByIdGameIn(ids);

        return montaDto(retorno != null ? retorno : new ArrayList<>());
    }

    private List<ItadBuscaPorTermoDto> validaInformacaoNoBanco(List<ItadBuscaPorTermo> lista, String termo) {

        LocalDateTime limite = LocalDateTime.now().minusSeconds(10);
        List<ItadBuscaPorTermo> atrasados = new ArrayList<>();

        for (ItadBuscaPorTermo e : lista) {
            if (e.getDataLastSearch() != null && e.getDataLastSearch().isBefore(limite)) {
                atrasados.add(e);
            }
        }

        if (!atrasados.isEmpty()) {
            return atualizaInformacoesNoBanco(atrasados, termo);
        }

        return montaDto(lista);
    }

    private List<ItadBuscaPorTermoDto> atualizaInformacoesNoBanco(List<ItadBuscaPorTermo> lista, String termo) {

        List<ItadBuscaPorTermoDto> api = buscaNaApi(termo);

        Map<String, ItadBuscaPorTermoDto> map = new HashMap<>();
        for (ItadBuscaPorTermoDto dto : api) {
            map.put(dto.getIdGame(), dto);
        }

        for (ItadBuscaPorTermo entity : lista) {

            ItadBuscaPorTermoDto dto = map.get(entity.getIdGame());

            if (dto != null) {

                entity.setDataLastSearch(LocalDateTime.now());
                entity.setNomeJogo(dto.getNomeJogo());
                entity.setSlug(dto.getSlug());
                entity.setTipoDoItem(dto.getTipoDoItem());

                if (entity.getAssetsItens() != null && dto.getItadAssetsLista() != null) {
                    entity.getAssetsItens().setImagem01(dto.getItadAssetsLista().getImagem01());
                    entity.getAssetsItens().setArteSecundaria(dto.getItadAssetsLista().getArteSecundaria());
                    entity.getAssetsItens().setDataLastSearch(LocalDateTime.now());
                }
            }
        }

        this.itadBuscaPorTermoRepository.saveAll(lista);
        return montaDto(lista);
    }

    public List<ItadBuscaPorTermoDto> montaDto(List<ItadBuscaPorTermo> lista) {

        if (lista == null) {
            return new ArrayList<>();
        }

        List<ItadBuscaPorTermoDto> retorno = new ArrayList<>();

        for (ItadBuscaPorTermo e : lista) {

            ItadAssetsLista assets = null;

            if (e.getAssetsItens() != null) {
                assets = new ItadAssetsLista(
                        e.getAssetsItens().getImagem01(),
                        e.getAssetsItens().getArteSecundaria());
            }

            retorno.add(new ItadBuscaPorTermoDto(
                    e.getIdGame(),
                    e.getSlug(),
                    e.getNomeJogo(),
                    e.getTipoDoItem(),
                    assets));
        }

        return retorno;
    }
}