package com.playsync.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.playsync.demo.dtoresponse.HomeResponseDTO;
import com.playsync.demo.dtoresponse.ItensFiltradosPeloTermoDTO;
import com.playsync.demo.dtoresponse.RawgGameEnrichDTO;
import com.playsync.demo.service.RawgService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api-playsync")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class RawgController {

	private final RawgService rawgService;

	/**
	 * Endpoint combinado: retorna featured + trending sem duplicatas
	 * GET /api-playsync/home?trendingLimit=10
	 */
	@GetMapping("/home")
	public HomeResponseDTO getHome(
			@RequestParam(defaultValue = "10") int trendingLimit) {
		return rawgService.getHomeData(trendingLimit).block();
	}

	/**
	 * Endpoint para buscar o jogo em destaque (mais popular)
	 * GET /api-playsync/featured
	 */
	@GetMapping("/featured")
	public ItensFiltradosPeloTermoDTO getFeatured() {
		return rawgService.getFeaturedGame().block();
	}

	/**
	 * Endpoint para buscar jogos em tendência
	 * GET /api-playsync/trending?limit=10
	 */
	@GetMapping("/trending")
	public List<ItensFiltradosPeloTermoDTO> getTrending(
			@RequestParam(defaultValue = "10") int limit) {
		return rawgService.getTrendingGames(limit).block();
	}

	/**
	 * Endpoint para buscar screenshots em alta resolução de um jogo pelo ID RAWG
	 * GET /api-playsync/games/{rawgId}/screenshots
	 * Retorna lista de URLs de imagens (1280x720 a 1920x1080)
	 */
	@GetMapping("/games/{rawgId}/screenshots")
	public List<String> getGameScreenshots(@PathVariable Long rawgId) {
		return rawgService.getGameScreenshots(rawgId).block();
	}

	/**
	 * Enriquece um jogo pelo nome: retorna rawgId + backgroundImage + screenshots
	 * GET /api-playsync/games/enrich?name={name}
	 * Usado para jogos vindos da busca Steam que não têm dados RAWG
	 */
	@GetMapping("/games/enrich")
	public RawgGameEnrichDTO enrichByName(@RequestParam String name) {
		return rawgService.enrichByName(name).block();
	}
}
