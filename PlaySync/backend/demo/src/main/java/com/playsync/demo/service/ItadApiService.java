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
            //persisteInformacaoNoBanco(buscaNaApi(termoString));
            return null;
        }
        return null;

    }

    private List<ItadBuscaPorTermoDto> buscaNaApi(String termoString) {
        return this.itadClient.buscarPorTermo(termoString).block();
    }

    private void validaInformacaoNoBanco(List<ItadBuscaPorTermo> listaDeTermos) {

    }

    private void persisteInformacaoNoBanco(List<ItadBuscaPorTermoDto> itenBuscaPorTermoDtos) {
        List<ItadBuscaPorTermo> listaDeEntidades = new ArrayList<>();
        for (ItadBuscaPorTermoDto itadBuscaPorTermoDto : itenBuscaPorTermoDtos) {
            ItadBuscaPorTermo itadBuscaPorTermo = new ItadBuscaPorTermo(itadBuscaPorTermoDto.getIdGame(),
                    itadBuscaPorTermoDto.getSlug(), itadBuscaPorTermoDto.getNomeJogo(),
                    itadBuscaPorTermoDto.getTipoDoItem(), LocalDateTime.now());
            ItadAssetsDeItens itadAssetsDeItens = new ItadAssetsDeItens(
                    itadBuscaPorTermoDto.getItadAssetsLista().getImagem01(),
                    itadBuscaPorTermoDto.getItadAssetsLista().getArteSecundaria(), itadBuscaPorTermo,
                    LocalDateTime.now());
            itadBuscaPorTermo.getAssetsItens().add(itadAssetsDeItens);
            listaDeEntidades.add(itadBuscaPorTermo);

        }
        this.itadBuscaPorTermoRepository.saveAll(listaDeEntidades);

    }

}
