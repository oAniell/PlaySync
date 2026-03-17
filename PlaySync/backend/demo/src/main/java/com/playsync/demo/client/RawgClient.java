package com.playsync.demo.client;

import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playsync.demo.dtoresponse.BuscaPorTermoDTO;

import reactor.core.publisher.Mono;

public class RawgClient {

    /*
     * /games?search=the%20last%20of%20us&key=a27c502d9b114cde87e03e215a3d39e3
     */

    private final WebClient webClient;

    public RawgClient(WebClient webClient) {
        this.webClient = webClient;
    }

    /*
     * https://api.rawg.io/api/games?search=the%20last%20of%20us&key=
     * a27c502d9b114cde87e03e215a3d39e3
     */

    public Mono<BuscaPorTermoDTO> buscarPorTermoRawg(String termo) {
    return this.webClient.get()
            .uri(uri -> uri.path("/games")
                    .queryParam("search", termo)
                    .queryParam("search_precise", true)
                    .queryParam("page_size", 10)
                    .queryParam("page", 1)
                    .queryParam("key", "a27c502d9b114cde87e03e215a3d39e3")
                    .queryParam("language", "pt")
                    .build())
            .retrieve()
            .bodyToMono(BuscaPorTermoDTO.class);
}
}
