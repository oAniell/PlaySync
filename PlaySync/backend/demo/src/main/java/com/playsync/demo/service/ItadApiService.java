package com.playsync.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import com.playsync.demo.Entities.ItadAssetsDeItens;
import com.playsync.demo.Entities.ItadBuscaPorTermo;
import com.playsync.demo.client.ItadClient;
import com.playsync.demo.dtoresponse.ItadAssetsLista;
import com.playsync.demo.dtoresponse.ItadBuscaPorTermoDto;
import com.playsync.demo.repository.ItadBuscaPorTermoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItadApiService {
    private final ItadClient itadClient;
    private final ItadBuscaPorTermoRepository itadBuscaPorTermoRepository;
    /* */

    public List<ItadBuscaPorTermoDto> principalMethod(String termoString) {
        List<ItadBuscaPorTermo> buscaNoBanco = this.itadBuscaPorTermoRepository.findByNome(termoString);
        if (buscaNoBanco == null || buscaNoBanco.isEmpty()) {
            return salvaInformacaoNoBanco(buscaNaApi(termoString));

        }
        return montaDto(buscaNoBanco);
    }

    private List<ItadBuscaPorTermoDto> buscaNaApi(String termoString) {
        return this.itadClient.buscarPorTermo(termoString).block();
    }

    private List<ItadBuscaPorTermoDto> salvaInformacaoNoBanco(List<ItadBuscaPorTermoDto> itadBuscaPorTermoDtos) {
        List<ItadBuscaPorTermo> listaDeEntidades = new ArrayList<>();
        for (ItadBuscaPorTermoDto buscaPorTermoDto : itadBuscaPorTermoDtos) {
            ItadBuscaPorTermo itadBuscaPorTermo = new ItadBuscaPorTermo(buscaPorTermoDto.getIdGame(),
                    buscaPorTermoDto.getSlug(), buscaPorTermoDto.getNomeJogo(), buscaPorTermoDto.getTipoDoItem(),
                    LocalDateTime.now(), null);
            ItadAssetsDeItens itadAssetsDeItens = new ItadAssetsDeItens(
                    buscaPorTermoDto.getItadAssetsLista().getImagem01(),
                    buscaPorTermoDto.getItadAssetsLista().getArteSecundaria(), buscaPorTermoDto.getIdGame(),
                    null, LocalDateTime.now());
            itadAssetsDeItens.setItadBuscaPorTermo(itadBuscaPorTermo);
            itadBuscaPorTermo.setAssetsItens(itadAssetsDeItens);
            listaDeEntidades.add(itadBuscaPorTermo);
        }
        this.itadBuscaPorTermoRepository.saveAll(listaDeEntidades);
        return montaDto(listaDeEntidades);
        // aqui tenho que validar em questao pois se o
        // usuario buscar por um jogo ja existente vai dar
        // duplicacao no banco, tenho que criar uma
        // validacao por id
    }

    private List<ItadBuscaPorTermoDto> validaInformacaoNoBanco(List<ItadBuscaPorTermo> buscaNoBanco, String termo) {
        LocalDateTime dataLimite = LocalDateTime.now().minusSeconds(10);
        List<ItadBuscaPorTermo> itensAtrasados = new ArrayList<>();
        for (ItadBuscaPorTermo itadBuscaPorTermo : buscaNoBanco) {
            if (itadBuscaPorTermo.getDataLastSearch().isBefore(dataLimite)) {
                itensAtrasados.add(itadBuscaPorTermo);
            }
        }
        if (!itensAtrasados.isEmpty()) {
            return atualizaInformacoesNoBanco(itensAtrasados, termo);
        }
        return montaDto(buscaNoBanco);

    }

    private List<ItadBuscaPorTermoDto> atualizaInformacoesNoBanco(List<ItadBuscaPorTermo> buscaNoBanco, String termo) {
        List<ItadBuscaPorTermoDto> buscaPorTermoDtos = buscaNaApi(termo);

        Map<String, ItadBuscaPorTermoDto> mapperDaApi = new HashMap<>();
        for (ItadBuscaPorTermoDto itadBuscaPorTermoDto : buscaPorTermoDtos) {
            mapperDaApi.put(itadBuscaPorTermoDto.getIdGame(), itadBuscaPorTermoDto);
        }
        for (ItadBuscaPorTermo itadBuscaPorTermo : buscaNoBanco) {
            ItadBuscaPorTermoDto itadBuscaPorTermoDto = mapperDaApi.get(itadBuscaPorTermo.getIdGame());
            if (itadBuscaPorTermoDto != null) {
                itadBuscaPorTermo.setDataLastSearch(LocalDateTime.now());
                itadBuscaPorTermo.setNomeJogo(itadBuscaPorTermoDto.getNomeJogo());
                itadBuscaPorTermo.setSlug(itadBuscaPorTermoDto.getSlug());
                itadBuscaPorTermo.setTipoDoItem(itadBuscaPorTermoDto.getTipoDoItem());
                itadBuscaPorTermo.getAssetsItens()
                        .setArteSecundaria(itadBuscaPorTermoDto.getItadAssetsLista().getArteSecundaria());
                itadBuscaPorTermo.getAssetsItens()
                        .setImagem01(itadBuscaPorTermoDto.getItadAssetsLista().getImagem01());
                itadBuscaPorTermo.getAssetsItens().setDataLastSearch(LocalDateTime.now());
            }

        }
        this.itadBuscaPorTermoRepository.saveAll(buscaNoBanco);
        return montaDto(buscaNoBanco);
    }

    public List<ItadBuscaPorTermoDto> montaDto(List<ItadBuscaPorTermo> buscaNoBanco) {
        List<ItadBuscaPorTermoDto> dtoRetorno = new ArrayList<>();
        for (ItadBuscaPorTermo buscaPorTermo : buscaNoBanco) {
            dtoRetorno.add(new ItadBuscaPorTermoDto(buscaPorTermo.getIdGame(), buscaPorTermo.getSlug(),
                    buscaPorTermo.getNomeJogo(), buscaPorTermo.getTipoDoItem(),
                    new ItadAssetsLista(buscaPorTermo.getAssetsItens().getImagem01(),
                            buscaPorTermo.getAssetsItens().getArteSecundaria())));

        }
        return dtoRetorno;
    }



    
}
