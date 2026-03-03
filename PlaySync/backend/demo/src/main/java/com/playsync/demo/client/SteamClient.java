package com.playsync.demo.client;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.playsync.demo.dtoresponse.BuscaPorTermoDTO;

import reactor.core.publisher.Mono;

@Service
public class SteamClient {

	private final WebClient webClient;

	public SteamClient(WebClient webClient) {
		this.webClient = webClient;
	}

	/*
	 * https://store.steampowered.com/api/storesearch/?term=TERMO&l=portuguese&cc=BR
	 */

	public Mono<BuscaPorTermoDTO> buscarPorTermo(String termo) {
		return this.webClient.get().
				uri(uri -> uri.path("/storesearch/")
						   .queryParam("name", termo)
						   .queryParam("l", "portuguese")
						   .queryParam("cc", "BR")
						   .build())
				.retrieve()
				.bodyToMono(BuscaPorTermoDTO.class);
	}

}