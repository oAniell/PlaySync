package com.playsync.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.playsync.demo.Entities.CheapSharkLojasApi;
import com.playsync.demo.dtoresponse.CheapSharkApiDto;
import com.playsync.demo.dtoresponse.GenerosApiRawgDTO;
import com.playsync.demo.dtoresponse.MergeCheapAndRawgResponse;
import com.playsync.demo.dtoresponse.MergeGenerosRawgAndCheapShark;
import com.playsync.demo.dtoresponse.MergeListaDeLojaEPrecoResponse;
import com.playsync.demo.dtoresponse.PlataformasDisponiveisViaRawgParaMerge;
import com.playsync.demo.dtoresponse.PlataformsRawg;
import com.playsync.demo.dtoresponse.RawgApiBuscaTermoDTO;
import com.playsync.demo.dtoresponse.TotalItensBuscadosRawgDTO;
import com.playsync.demo.repository.CheapSharkLojasApiRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MergeServiceCheapSharkAndRawg {

    private final RawgApiService rawgApiService;
    private final CheapSharkApiService cheapSharkApiService;
    private final CheapSharkLojasApiRepository cheapSharkLojasApiRepository;

    public List<MergeCheapAndRawgResponse> mergearRawgECheapShark(String termoEmComum) {

        TotalItensBuscadosRawgDTO rawgResponse = rawgApiService.principalMethod(termoEmComum);
        List<CheapSharkApiDto> cheapResponse = cheapSharkApiService.principalMethod(termoEmComum);
        List<CheapSharkLojasApi> lojasBanco = cheapSharkLojasApiRepository.consultarTabela();

        Map<String, List<CheapSharkApiDto>> mapaJogosCheap = mapearJogosCheapShark(cheapResponse);
        Map<Long, CheapSharkLojasApi> mapaLojas = mapearLojas(lojasBanco);

        List<MergeCheapAndRawgResponse> mergeFinal = new ArrayList<>();

        for (RawgApiBuscaTermoDTO rawgJogo : rawgResponse.getRawgApiBuscaTermo()) {
            List<MergeListaDeLojaEPrecoResponse> lojasEPrecos = buscarLojasEPrecos(rawgJogo, mapaJogosCheap, mapaLojas);
            List<PlataformasDisponiveisViaRawgParaMerge> plataformas = extrairPlataformas(rawgJogo);
            List<MergeGenerosRawgAndCheapShark> generos = extrairGeneros(rawgJogo);
            MergeCheapAndRawgResponse response = new MergeCheapAndRawgResponse(
                    rawgJogo.getNome(),
                    rawgJogo.getDataLancamento(),
                    rawgJogo.getImgBackground(),
                    rawgJogo.getIdGame(),
                    rawgJogo.getNotaMediaJogo(),
                    rawgJogo.getNumeroAvaliacoes(),
                    lojasEPrecos,
                    plataformas,
                    generos);

            mergeFinal.add(response);
        }

        return mergeFinal;
    }

    private Map<String, List<CheapSharkApiDto>> mapearJogosCheapShark(List<CheapSharkApiDto> lista) {
        Map<String, List<CheapSharkApiDto>> mapa = new HashMap<>();
        for (CheapSharkApiDto dto : lista) {
            String nome = dto.getNomeJogo().toLowerCase();
            if (!mapa.containsKey(nome)) {
                mapa.put(nome, new ArrayList<>());
            }
            mapa.get(nome).add(dto);
        }

        return mapa;
    }

    private Map<Long, CheapSharkLojasApi> mapearLojas(List<CheapSharkLojasApi> lojas) {
        Map<Long, CheapSharkLojasApi> mapa = new HashMap<>();
        for (CheapSharkLojasApi loja : lojas) {
            mapa.put(loja.getIdLoja(), loja);
        }

        return mapa;
    }

    private List<MergeListaDeLojaEPrecoResponse> buscarLojasEPrecos(
            RawgApiBuscaTermoDTO rawgJogo,
            Map<String, List<CheapSharkApiDto>> mapaJogosCheap,
            Map<Long, CheapSharkLojasApi> mapaLojas) {

        List<MergeListaDeLojaEPrecoResponse> lista = new ArrayList<>();

        String nomeRawg = rawgJogo.getNome().toLowerCase();

        for (String nomeCheap : mapaJogosCheap.keySet()) {

            if (nomeCheap.contains(nomeRawg) || nomeRawg.contains(nomeCheap)) {

                List<CheapSharkApiDto> precos = mapaJogosCheap.get(nomeCheap);

                for (CheapSharkApiDto preco : precos) {
                    CheapSharkLojasApi loja = mapaLojas.get(preco.getStoreId());

                    if (loja != null) {
                        lista.add(new MergeListaDeLojaEPrecoResponse(
                                loja.getNomeLoja(),
                                preco.getPrecoAtual(),
                                preco.getPrecoOriginal(),
                                preco.getDesconto()));
                    }
                }
            }
        }

        return lista;
    }

    private List<PlataformasDisponiveisViaRawgParaMerge> extrairPlataformas(
            RawgApiBuscaTermoDTO rawgJogo) {

        List<PlataformasDisponiveisViaRawgParaMerge> lista = new ArrayList<>();

        for (PlataformsRawg plataforma : rawgJogo.getPlataformas()) {
            lista.add(new PlataformasDisponiveisViaRawgParaMerge(
                    plataforma.getPlataformasRawgDTO().getIdPlataforma(),
                    plataforma.getPlataformasRawgDTO().getPlataforma()));
        }

        return lista;
    }

    private List<MergeGenerosRawgAndCheapShark> extrairGeneros(
            RawgApiBuscaTermoDTO rawgJogo) {

        List<MergeGenerosRawgAndCheapShark> lista = new ArrayList<>();

        for (GenerosApiRawgDTO genero : rawgJogo.getGenerosApiRawgDTOs()) {
            lista.add(new MergeGenerosRawgAndCheapShark(
                    genero.getIdGeneros(),
                    genero.getNome()));
        }

        return lista;
    }
}