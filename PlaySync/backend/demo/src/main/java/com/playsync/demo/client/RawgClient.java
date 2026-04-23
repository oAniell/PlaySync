package com.playsync.demo.client;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.playsync.demo.dtoresponse.RawgGameResponse;
import com.playsync.demo.dtoresponse.RawgScreenshotListResponse;

import reactor.core.publisher.Mono;

@Service
public class RawgClient {

	private static final Logger log = LoggerFactory.getLogger(RawgClient.class);

	private final WebClient rawgWebClient;
	private final String apiKey;

	public RawgClient(@Qualifier("rawgCliente") WebClient rawgCliente, @Value("${rawg.api.key}") String apiKey) {
		this.rawgWebClient = rawgCliente;
		this.apiKey = apiKey;
	}

	private <T> Mono<T> handleErrors(Mono<T> mono) {
		return mono.onErrorResume(ex -> {
			log.warn("RAWG API error: {}", ex.getMessage());
			return Mono.empty();
		});
	}

	/**
	 * Busca jogos populares ordenados por popularidade
	 * Endpoint: https://api.rawg.io/api/games?key=API_KEY&ordering=-added
	 */
	public Mono<RawgGameResponse> getPopularGames(int pageSize) {
		return handleErrors(this.rawgWebClient.get()
				.uri(uri -> uri.path("/games")
						.queryParam("key", apiKey)
						.queryParam("ordering", "-added")
						.queryParam("page_size", pageSize)
						.build())
				.retrieve()
				.bodyToMono(RawgGameResponse.class));
	}

	/**
	 * Busca jogos em tendência (mais jogados nos últimos 12 meses)
	 * Endpoint: https://api.rawg.io/api/games?key=API_KEY&dates={12_meses_atras},{hoje}&ordering=-added
	 */
	public Mono<RawgGameResponse> getTrendingGames(int pageSize) {
		String hoje = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		String seisMesesAtras = LocalDate.now().minusMonths(6).format(DateTimeFormatter.ISO_LOCAL_DATE);
		String dateRange = seisMesesAtras + "," + hoje;

		return handleErrors(this.rawgWebClient.get()
				.uri(uri -> uri.path("/games")
						.queryParam("key", apiKey)
						.queryParam("dates", dateRange)
						.queryParam("ordering", "-added")
						.queryParam("page_size", pageSize)
						.build())
				.retrieve()
				.bodyToMono(RawgGameResponse.class));
	}

	/**
	 * Busca jogo por nome exato
	 * Endpoint: https://api.rawg.io/api/games?key=API_KEY&search={name}&page_size=1&exact=true
	 */
	public Mono<RawgGameResponse> searchGameByName(String name) {
		return handleErrors(this.rawgWebClient.get()
				.uri(uri -> uri.path("/games")
						.queryParam("key", apiKey)
						.queryParam("search", name)
						.queryParam("page_size", 1)
						.queryParam("search_exact", true)
						.build())
				.retrieve()
				.bodyToMono(RawgGameResponse.class));
	}

	public Mono<RawgGameResponse> searchGameCandidates(String name) {
		return handleErrors(this.rawgWebClient.get()
				.uri(uri -> uri.path("/games")
						.queryParam("key", apiKey)
						.queryParam("search", name)
						.queryParam("page_size", 5)
						.queryParam("stores", 1)
						.build())
				.retrieve()
				.bodyToMono(RawgGameResponse.class));
	}

	public Mono<Object> getGameDetails(Long gameId) {
		return handleErrors(this.rawgWebClient.get()
				.uri(uri -> uri.path("/games/{id}")
						.queryParam("key", apiKey)
						.build(gameId))
				.retrieve()
				.bodyToMono(Object.class));
	}

	public Mono<RawgScreenshotListResponse> getGameScreenshots(Long gameId) {
		return handleErrors(this.rawgWebClient.get()
				.uri(uri -> uri.path("/games/{id}/screenshots")
						.queryParam("key", apiKey)
						.build(gameId))
				.retrieve()
				.bodyToMono(RawgScreenshotListResponse.class));
	}
}
