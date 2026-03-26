package com.playsync.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
	/*
	 * *
	 * https://store.steampowered.com/api/storesearch/?term=TERMO&l=portuguese&cc=BR
	 * 
	 */
	@Bean
	public WebClient webClient(WebClient.Builder builder) {
		return builder.baseUrl("https://store.steampowered.com/api") // api steam
				.defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
				.build();
	}

	@Bean
	public WebClient rawgCliente(WebClient.Builder builder) {
		return builder.baseUrl("https://api.rawg.io/api").build();
	}

	@Bean
	public WebClient cheapSharkApiCliente(WebClient.Builder builder) {
		return builder.baseUrl("https://www.cheapshark.com/api/1.0").build();
	}

	/*
	 * https://economia.awesomeapi.com.br/json/last/USD-BRL -- cotacao do dolar
	 */
	@Bean
	public WebClient cotacaoDolarAwesomeApiCliente(WebClient.Builder builder) {
		return builder.baseUrl("https://economia.awesomeapi.com.br/json").build();
	}
	/*
	 * aqui esta centralizado todas as BASEURLs que utilizamos, desde a cotacao do
	 * dolar ate as APIs em questao da busca de jogos nas plataformas.
	 * 
	 */
}