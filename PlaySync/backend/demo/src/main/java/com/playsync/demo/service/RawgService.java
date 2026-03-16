package com.playsync.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.playsync.demo.client.RawgClient;
import com.playsync.demo.dtoresponse.ItensFiltradosPeloTermoDTO;
import com.playsync.demo.dtoresponse.RawgGame;
import com.playsync.demo.dtoresponse.RawgGameDetailDTO;
import com.playsync.demo.dtoresponse.RawgGameResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class RawgService {

	private final RawgClient rawgClient;

	/**
	 * Busca o jogo em destaque (mais popular)
	 * Retorna apenas o primeiro jogo da lista de jogos populares
	 */
	public Mono<ItensFiltradosPeloTermoDTO> getFeaturedGame() {
		return rawgClient.getPopularGames(1)
				.map(response -> {
					if (response.getResults() == null || response.getResults().isEmpty()) {
						return null;
					}
					return mapToDto(response.getResults().get(0));
				});
	}

	/**
	 * Busca jogos em tendência (mais populares)
	 * Retorna lista de jogos populares
	 */
	public Mono<List<ItensFiltradosPeloTermoDTO>> getTrendingGames(int limit) {
		return rawgClient.getTrendingGames(limit)
				.map(response -> {
					if (response.getResults() == null || response.getResults().isEmpty()) {
						return List.of();
					}
					return response.getResults().stream()
							.map(this::mapToDto)
							.collect(Collectors.toList());
				});
	}

	/**
	 * Converte RawgGame para ItensFiltradosPeloTermoDTO
	 * Formato que o frontend espera
	 */
	private ItensFiltradosPeloTermoDTO mapToDto(RawgGame game) {
		ItensFiltradosPeloTermoDTO dto = new ItensFiltradosPeloTermoDTO();
		
		dto.setIdGame(game.getId().intValue());
		dto.setName(game.getName());
		dto.setImg(game.getBackgroundImage());
		
		// Cria o objeto de detalhes da RAWG
		RawgGameDetailDTO rawgDetails = new RawgGameDetailDTO();
		
		// Gêneros como string
		if (game.getGenres() != null && !game.getGenres().isEmpty()) {
			String genres = game.getGenres().stream()
					.map(g -> g.getName())
					.collect(Collectors.joining(", "));
			rawgDetails.setNomeGeneros(genres);
		}
		
		// Plataformas como string
		if (game.getPlatforms() != null && !game.getPlatforms().isEmpty()) {
			String platforms = game.getPlatforms().stream()
					.map(p -> p.getPlatform() != null ? p.getPlatform().getName() : null)
					.filter(name -> name != null)
					.collect(Collectors.joining(", "));
			rawgDetails.setNomePlataformas(platforms);
		}
		
		// Data de lançamento
		rawgDetails.setDataLancamento(game.getReleased());
		
		// Avaliação
		rawgDetails.setAvaliacao(game.getRating());
		
		// Define os detalhes da RAWG no DTO principal
		dto.setRawgDetails(rawgDetails);
		
		return dto;
	}
}
