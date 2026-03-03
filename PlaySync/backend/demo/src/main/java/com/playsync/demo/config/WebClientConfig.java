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
		return builder.
				 baseUrl("store.steampowered.com/api")
				.build();
	}
}