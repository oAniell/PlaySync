package com.playsync.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.playsync.demo.client.RawgClient;
import com.playsync.demo.dtoresponse.HomeResponseDTO;
import com.playsync.demo.dtoresponse.ItensFiltradosPeloTermoDTO;
import com.playsync.demo.dtoresponse.RawgGame;
import com.playsync.demo.dtoresponse.RawgGameDetailDTO;
import com.playsync.demo.dtoresponse.RawgGameResponse;
import com.playsync.demo.repository.ItensBuscadosPeloTermoRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class RawgService {

	private final RawgClient rawgClient;
	private final ItensBuscadosPeloTermoRepository itensRepository;

	/**
	 * Busca o jogo em destaque baseado na atividade do site (último 30 dias).
	 * Pega o jogo mais pesquisado pelos usuários e busca seus detalhes no RAWG.
	 * Fallback: primeiro jogo do trending da RAWG caso não haja buscas suficientes.
	 */
	public Mono<ItensFiltradosPeloTermoDTO> getFeaturedGame() {
		LocalDateTime trintaDiasAtras = LocalDateTime.now().minusDays(30);
		List<String> maisSearchados = itensRepository.findMostSearchedGameNames(
				trintaDiasAtras, PageRequest.of(0, 1));

		if (!maisSearchados.isEmpty()) {
			return rawgClient.searchGameByName(maisSearchados.get(0))
					.flatMap(response -> {
						if (response.getResults() != null && !response.getResults().isEmpty()) {
							return Mono.just(mapToDto(response.getResults().get(0)));
						}
						return getFeaturedFallback();
					});
		}

		return getFeaturedFallback();
	}

	private Mono<ItensFiltradosPeloTermoDTO> getFeaturedFallback() {
		return rawgClient.getTrendingGames(2)
				.map(response -> {
					if (response.getResults() == null || response.getResults().isEmpty()) {
						return null;
					}
					// índice 0 será o primeiro da lista de trending abaixo — usa o índice 1 para não repetir
					int idx = response.getResults().size() > 1 ? 1 : 0;
					return mapToDto(response.getResults().get(idx));
				});
	}

	/**
	 * Retorna featured + trending em uma única chamada coordenada.
	 * Garante que o jogo em destaque não aparece na lista de trending.
	 */
	public Mono<HomeResponseDTO> getHomeData(int trendingLimit) {
		return getFeaturedGame().flatMap(featured ->
			rawgClient.getTrendingGames(trendingLimit + 1)
				.map(response -> {
					List<ItensFiltradosPeloTermoDTO> trending;
					if (response.getResults() == null || response.getResults().isEmpty()) {
						trending = List.of();
					} else {
						trending = response.getResults().stream()
								.map(this::mapToDto)
								.filter(g -> featured == null || !g.getName().equalsIgnoreCase(featured.getName()))
								.limit(trendingLimit)
								.collect(Collectors.toList());
					}
					return new HomeResponseDTO(featured, trending);
				})
		);
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
