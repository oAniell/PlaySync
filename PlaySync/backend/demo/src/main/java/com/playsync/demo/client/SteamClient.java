package com.playsync.demo.client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
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

	/**
	 * Busca screenshots em 1920x1080 pelo Steam App ID.
	 * Endpoint: https://store.steampowered.com/api/appdetails?appids={id}&filters=screenshots
	 * Resposta é um JSON dinâmico keyed pelo appId — parseado via JsonNode.
	 */
	public Mono<List<String>> getScreenshots(Integer appId) {
		return this.webClient.get()
			.uri(uri -> uri.path("/appdetails")
						   .queryParam("appids", appId)
						   .queryParam("filters", "screenshots")
						   .build())
			.retrieve()
			.bodyToMono(String.class)
			.map(json -> {
				try {
					JsonNode root = new ObjectMapper().readTree(json);
					JsonNode appData = root.get(String.valueOf(appId));
					if (appData == null || !appData.path("success").asBoolean()) return List.<String>of();
					JsonNode screenshotsNode = appData.path("data").path("screenshots");
					List<String> urls = new ArrayList<>();
					for (JsonNode s : screenshotsNode) {
						String url = s.path("path_full").asText("");
						if (!url.isEmpty()) urls.add(url);
					}
					return urls;
				} catch (Exception e) {
					return List.<String>of();
				}
			});
	}
	}

