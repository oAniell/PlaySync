package com.playsync.demo.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

    /*
     * https://store.steampowered.com/api/storesearch/?term=TERMO&l=portuguese&cc=BR
     */
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.baseUrl("https://store.steampowered.com/api")
                .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofSeconds(30))
                ))
                .build();
    }

    /*
     * POST https://api.isthereanydeal.com/games/prices/v3?key=KEY&country=BR
     * GET  https://api.isthereanydeal.com/games/search/v1?key=KEY&title=TERMO
     */
    @Bean
    public WebClient buscaPorTermoITADCliente(WebClient.Builder builder) {
        return builder.baseUrl("https://api.isthereanydeal.com/games").build();
    }

    /*
     * https://api.rawg.io/api
     */
    @Bean
    public WebClient rawgCliente(WebClient.Builder builder) {
        return builder.baseUrl("https://api.rawg.io/api").build();
    }
}
