package com.playsync.demo.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.playsync.demo.dtoresponse.CotacaoDolarResponse;

import reactor.core.publisher.Mono;

@Service
public class AwesomeApiCotacaoDolarClient {

    private final WebClient webClient;

    public AwesomeApiCotacaoDolarClient(@Qualifier("cotacaoDolarAwesomeApiCliente") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<CotacaoDolarResponse> buscarCotacaoDolar() {
        return webClient.get().uri(uri -> uri.path("/last/USD-BRL").build()).retrieve()
                .bodyToMono(CotacaoDolarResponse.class);
    }

    /*
     * 
     * o client deixamos responsavel a parte da CHAMADA da API e convertando o JSON
     * enviado da APi para objeto Java, onde mapeamos por um DTO com campos
     * especificos e relevantes para nossa aplicacao
     */
}
