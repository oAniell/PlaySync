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
	    return builder.baseUrl("https://store.steampowered.com/api")
	            .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
	            .build();
	}
}