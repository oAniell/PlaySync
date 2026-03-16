package com.playsync.demo.client;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
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
	    return this.webClient.get()
	        .uri(uri -> uri.path("/storesearch/")
	                       .queryParam("term", termo)
	                       .queryParam("l", "portuguese")
	                       .queryParam("cc", "BR")
	                       .build())
	        .retrieve()
	        .bodyToMono(String.class) // pega primeiro como String
	        .map(json -> {
	            try {
	                // Log da resposta JSON da Steam para debug
	                System.out.println("=== RESPOSTA BRUTA DA STEAM API ===");
	                System.out.println(json);
	                System.out.println("=====================================");
	                
	                return new ObjectMapper().readValue(json, BuscaPorTermoDTO.class);
	            } catch (Exception e) {
	                throw new RuntimeException("Erro ao mapear JSON da Steam", e);
	            }
	        });
	}
	}

