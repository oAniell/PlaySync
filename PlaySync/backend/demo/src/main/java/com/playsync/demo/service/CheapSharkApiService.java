package com.playsync.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.playsync.demo.Entities.CheapSharkJogosEPrecosApi;
import com.playsync.demo.Entities.CheapSharkLojasApi;
import com.playsync.demo.Entities.RawgApiBuscaTermo;
import com.playsync.demo.client.CheapSharkClient;
import com.playsync.demo.dtoresponse.CheapSharkApiDto;
import com.playsync.demo.dtoresponse.CheapSharkApiStoresDto;
import com.playsync.demo.dtoresponse.RawgApiBuscaTermoDTO;
import com.playsync.demo.repository.CheapSharkJogosEPrecosApiRepository;
import com.playsync.demo.repository.CheapSharkLojasApiRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CheapSharkApiService {

    private final CheapSharkClient cheapSharkClient;
    private final CheapSharkLojasApiRepository cheapSharkLojasApiRepository;
    private final CheapSharkJogosEPrecosApiRepository cheapSharkJogosEPrecosApiRepository;

    public List<CheapSharkApiDto> principalMethod(String termo) {
        List<CheapSharkJogosEPrecosApi> cheapSharkApiBanco = this.cheapSharkJogosEPrecosApiRepository
                .selectByTerm(termo);
        if (cheapSharkApiBanco == null || cheapSharkApiBanco.isEmpty()) {
            return validaTermo(termo);
        }
        return montaDto(cheapSharkApiBanco);
    }

    // testes de requisicoes
    public List<CheapSharkApiDto> pegarInformacoesNaApi(String termo) {
        return this.cheapSharkClient.buscarPrecoCheapShark(termo).block();
    }

    /*
     * 3
     * 
     * 
     * CONSTRUIR METODO QUE TEM O MESMO COMPORTAMENTO DO SERVIDO DO RAWG, ONDE TEM
     * CACHE DE API E EVITA BUSCAR TODA VEZ NA API
     * (duas opcoes, toda vez que requisitar nossa API apagar as informacoes no
     * banco e recriar elas com a pesquisa ATualmente(custo é chamada de API
     * gigante)
     * ou fazer a mesma regra do rawg aplicar aqui, porem o codigo vai ficar
     * extenso))
     * 
     * 
     * 
     * 
     */

    @Transactional
    public List<CheapSharkApiDto> validaTermo(String termo) {
        List<CheapSharkApiDto> cheapSharkApiDtos = pegarInformacoesNaApi(termo);
        List<CheapSharkLojasApi> cheapSharkLojasApis = validaBancoDeDados();
        List<CheapSharkJogosEPrecosApi> cheapSharkJogosEPrecosApis = new ArrayList<>();
        if (cheapSharkApiDtos != null && !cheapSharkApiDtos.isEmpty()) {
            for (CheapSharkApiDto cheapSharkApiDto : cheapSharkApiDtos) {
                CheapSharkJogosEPrecosApi cheapSharkJogosEPrecosApi = new CheapSharkJogosEPrecosApi(
                        cheapSharkApiDto.getNomeJogo(),
                        cheapSharkApiDto.getPrecoAtual(), cheapSharkApiDto.getPrecoOriginal(),
                        cheapSharkApiDto.getDesconto(), cheapSharkApiDto.getStoreId(), null);

                for (CheapSharkLojasApi cheapSharkLojasApi : cheapSharkLojasApis) {
                    if (cheapSharkApiDto.getStoreId().equals(cheapSharkLojasApi.getIdLoja())) {
                        cheapSharkJogosEPrecosApi.setCheapSharkLojasApi(cheapSharkLojasApi);
                        cheapSharkLojasApi.getCheapSharkJogosEPrecos().add(cheapSharkJogosEPrecosApi);
                    }

                }
                cheapSharkJogosEPrecosApis.add(cheapSharkJogosEPrecosApi);

            }
            this.cheapSharkJogosEPrecosApiRepository.saveAll(cheapSharkJogosEPrecosApis);
            return montaDto(cheapSharkJogosEPrecosApis);

        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Nao foi encontrado resultado de precos com esse termo");
    }

    // testes de requisicoes
    public List<CheapSharkApiStoresDto> pegarLojas() {
        return this.cheapSharkClient.buscarLojasCheapShark().block();
    }

    @Transactional
    public List<CheapSharkLojasApi> validaBancoDeDados() {
        List<CheapSharkLojasApi> buscaNoBanco = this.cheapSharkLojasApiRepository.consultarTabela();
        if (buscaNoBanco == null || buscaNoBanco.isEmpty()) {
            buscaNoBanco = persisteInformacaoNoBancoDeDados(pegarLojas());
            return buscaNoBanco;
        }
        return buscaNoBanco;
    }

    private List<CheapSharkLojasApi> persisteInformacaoNoBancoDeDados(List<CheapSharkApiStoresDto> listaVindoDaApi) {
        List<CheapSharkLojasApi> listaDeEntidades = new ArrayList<>();
        for (CheapSharkApiStoresDto cheapSharkApiDto : listaVindoDaApi) {
            if (cheapSharkApiDto.getEstaAtivoOuNao().equals(1)) {
                listaDeEntidades
                        .add(new CheapSharkLojasApi(cheapSharkApiDto.getIdLoja(), cheapSharkApiDto.getNomeLoja()));
            }
        }
        this.cheapSharkLojasApiRepository.saveAll(listaDeEntidades);
        return listaDeEntidades;
    }

    public List<CheapSharkApiDto> montaDto(List<CheapSharkJogosEPrecosApi> lista) {
        List<CheapSharkApiDto> listaDto = new ArrayList<>();

        for (CheapSharkJogosEPrecosApi cheapSharkJogosEPrecosApi : lista) {
            listaDto.add(new CheapSharkApiDto(cheapSharkJogosEPrecosApi.getNomeJogo(),
                    cheapSharkJogosEPrecosApi.getPrecoAtual(), cheapSharkJogosEPrecosApi.getPrecoOriginal(),
                    cheapSharkJogosEPrecosApi.getDesconto(), cheapSharkJogosEPrecosApi.getStoreId()));
        }
        return listaDto;
    }

}
