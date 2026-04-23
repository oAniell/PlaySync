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
import com.playsync.demo.dtoresponse.RawgGameEnrichDTO;
import com.playsync.demo.dtoresponse.RawgGameResponse;
import com.playsync.demo.dtoresponse.RawgScreenshot;
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

		// Short screenshots (640x360) disponíveis no endpoint de lista — usados como prévia
		if (game.getShortScreenshots() != null && !game.getShortScreenshots().isEmpty()) {
			List<String> screenshotUrls = game.getShortScreenshots().stream()
					.map(RawgScreenshot::getImage)
					.filter(img -> img != null && !img.isBlank())
					.collect(Collectors.toList());
			dto.setScreenshots(screenshotUrls);
		}

		// Extrai Steam App ID das stores para montar capsule image (header.jpg com logo do jogo)
		if (game.getStores() != null) {
			game.getStores().stream()
					.filter(s -> s.getUrl() != null && s.getUrl().contains("store.steampowered.com/app/"))
					.findFirst()
					.ifPresent(s -> {
						String[] parts = s.getUrl().split("/app/");
						if (parts.length > 1) {
							String appIdStr = parts[1].replaceAll("[^0-9].*", "");
							if (!appIdStr.isEmpty()) {
								dto.setSteamCapsuleUrl(
									"https://cdn.akamai.steamstatic.com/steam/apps/" + appIdStr + "/header.jpg"
								);
							}
						}
					});
		}

		return dto;
	}

	/**
	 * Busca por nome no RAWG e retorna rawgId + background_image + short_screenshots.
	 * Busca até 5 candidatos e escolhe o que mais se parece com o nome original.
	 * Retorna null se nenhum candidato passar no threshold de similaridade.
	 */
	public Mono<RawgGameEnrichDTO> enrichByName(String name) {
		return rawgClient.searchGameCandidates(name)
				.map(response -> {
					if (response.getResults() == null || response.getResults().isEmpty()) {
						return null;
					}
					RawgGame best = response.getResults().stream()
							.filter(g -> g.getName() != null && nameSimilar(g.getName(), name))
							.findFirst()
							.orElse(null);

					if (best == null) return null;

					List<String> screenshots = List.of();
					if (best.getShortScreenshots() != null) {
						screenshots = best.getShortScreenshots().stream()
								.map(RawgScreenshot::getImage)
								.filter(img -> img != null && !img.isBlank())
								.collect(Collectors.toList());
					}
					return new RawgGameEnrichDTO(best.getId(), best.getBackgroundImage(), screenshots);
				});
	}

	/**
	 * Verifica se dois nomes de jogo são similares o suficiente para serem o mesmo título.
	 * Normaliza (lowercase, sem pontuação) e checa se um contém o outro.
	 */
	private boolean nameSimilar(String rawgName, String searchName) {
		String r = normalize(rawgName);
		String s = normalize(searchName);
		if (r.equals(s)) return true;
		// Aceita se um contém o outro e a diferença de tamanho é razoável (evita falsos como "LEGO X" vs "X")
		if (r.contains(s) || s.contains(r)) {
			int lenDiff = Math.abs(r.length() - s.length());
			int minLen = Math.min(r.length(), s.length());
			return lenDiff <= minLen; // diferença não pode ser maior que o próprio nome menor
		}
		return false;
	}

	private String normalize(String s) {
		return s.toLowerCase().replaceAll("[^a-z0-9\\s]", "").trim();
	}

	/**
	 * Busca screenshots em alta resolução (1280x720 a 1920x1080) de um jogo via endpoint dedicado.
	 * Endpoint RAWG: GET /api/games/{id}/screenshots
	 */
	public Mono<List<String>> getGameScreenshots(Long rawgGameId) {
		return rawgClient.getGameScreenshots(rawgGameId)
				.map(response -> {
					if (response.getResults() == null || response.getResults().isEmpty()) {
						return List.<String>of();
					}
					return response.getResults().stream()
							.map(RawgScreenshot::getImage)
							.filter(img -> img != null && !img.isBlank())
							.collect(Collectors.toList());
				});
	}
}
