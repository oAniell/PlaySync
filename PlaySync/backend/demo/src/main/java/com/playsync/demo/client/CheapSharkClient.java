package com.playsync.demo.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.playsync.demo.dtoresponse.CheapSharkApiDto;
import com.playsync.demo.dtoresponse.CheapSharkApiStoresDto;

import reactor.core.publisher.Mono;

@Service
public class CheapSharkClient {
    private final WebClient webClient;

    public CheapSharkClient(@Qualifier("cheapSharkApiCliente") WebClient webClient) {
        this.webClient = webClient;
    }

    /*
     * * API CHEAPSHARK FREE https://www.cheapshark.com/api/1.0/deals?title=elden
     * ring
     */ public Mono<List<CheapSharkApiDto>> buscarPrecoCheapShark(String termo) {
        return webClient.get()
                .uri(uri -> uri.path("/deals")
                        .queryParam("title", termo)
                        .build())
                .retrieve()
                .bodyToFlux(CheapSharkApiDto.class)
                .collectList();
    }

    public Mono<List<CheapSharkApiStoresDto>> buscarLojasCheapShark() {
        return webClient.get().uri(uri -> uri.path("/stores").build()).retrieve()
                .bodyToFlux(CheapSharkApiStoresDto.class).collectList();
    }

    /*
    Todos os filtros de API estao centralizadas aqui no package de client. */
}