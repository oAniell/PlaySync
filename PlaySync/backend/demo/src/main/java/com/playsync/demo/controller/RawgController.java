package com.playsync.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.playsync.demo.dtoresponse.ItensFiltradosPeloTermoDTO;
import com.playsync.demo.service.RawgService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api-playsync")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class RawgController {

	private final RawgService rawgService;

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
}
