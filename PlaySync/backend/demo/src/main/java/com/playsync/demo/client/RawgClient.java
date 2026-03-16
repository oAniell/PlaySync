package com.playsync.demo.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.playsync.demo.dtoresponse.RawgGameResponse;

import reactor.core.publisher.Mono;

@Service
public class RawgClient {

	private final WebClient rawgWebClient;
	private final String apiKey;

	public RawgClient(WebClient rawgCliente, @Value("${rawg.api.key}") String apiKey) {
		this.rawgWebClient = rawgCliente;
		this.apiKey = apiKey;
	}

	/**
	 * Busca jogos populares ordenados por popularidade
	 * Endpoint: https://api.rawg.io/api/games?key=API_KEY&ordering=-added
	 */
	public Mono<RawgGameResponse> getPopularGames(int pageSize) {
		return this.rawgWebClient.get()
				.uri(uri -> uri.path("/games")
						.queryParam("key", apiKey)
						.queryParam("ordering", "-added")
						.queryParam("page_size", pageSize)
						.build())
				.retrieve()
				.bodyToMono(RawgGameResponse.class);
	}

	/**
	 * Busca jogos em tendência (mais jogados nos últimos tempo)
	 * Endpoint: https://api.rawg.io/api/games?key=API_KEY&dates=2024-01-01,2025-12-31&ordering=-added
	 */
	public Mono<RawgGameResponse> getTrendingGames(int pageSize) {
		return this.rawgWebClient.get()
				.uri(uri -> uri.path("/games")
						.queryParam("key", apiKey)
						.queryParam("dates", "2024-01-01,2025-12-31")
						.queryParam("ordering", "-added")
						.queryParam("page_size", pageSize)
						.build())
				.retrieve()
				.bodyToMono(RawgGameResponse.class);
	}

	/**
	 * Busca detalhes de um jogo específico
	 * Endpoint: https://api.rawg.io/api/games/{id}?key=API_KEY
	 */
	public Mono<Object> getGameDetails(Long gameId) {
		return this.rawgWebClient.get()
				.uri(uri -> uri.path("/games/{id}")
						.queryParam("key", apiKey)
						.build(gameId))
				.retrieve()
				.bodyToMono(Object.class);
	}
}
