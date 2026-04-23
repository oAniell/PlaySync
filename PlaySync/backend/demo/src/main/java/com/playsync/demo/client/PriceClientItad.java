package com.playsync.demo.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.playsync.demo.dtoresponse.ItadMainClassDto;

import reactor.core.publisher.Mono;

@Service
public class PriceClientItad {

    @Qualifier("buscaPorTermoITADCliente")
    private final WebClient webClient;

    public PriceClientItad(@Qualifier("buscaPorTermoITADCliente") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<List<ItadMainClassDto>> buscarPrecos(List<String> ids) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/prices/v3")
                        .queryParam("key", "baf038a8b8a8039c1e5f0def483913f055640f6e")
                        .queryParam("country", "BR")
                        .build())
                .bodyValue(ids)
                .retrieve()
                .bodyToFlux(ItadMainClassDto.class)
                .collectList();
    }

}
