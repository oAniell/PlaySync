package com.playsync.demo.controller;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.playsync.demo.dtoresponse.BuscaPorTermoDTO;
import com.playsync.demo.dtoresponse.CheapSharkApiDto;
import com.playsync.demo.dtoresponse.MergerCheapSharkGamesAndStores;
import com.playsync.demo.dtoresponse.TotalItensBuscadosRawgDTO;
import com.playsync.demo.service.ApiSteam;
import com.playsync.demo.service.CheapSharkApiService;
import com.playsync.demo.service.RawgApiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api-playsync")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174" })
public class BuscaPorTermoController {

	private final ApiSteam api;
	private final RawgApiService rawgApi;
	private final CheapSharkApiService cheapSharkApi;

	@PostMapping("/search")
	public BuscaPorTermoDTO buscar(@RequestParam String termo) {
		System.out.println("Recebida requisicao de busca para termo: " + termo);
		BuscaPorTermoDTO result = this.api.buscaPorTermo(termo);
		System.out.println("Resultado retornado: " + result);
		System.out.println("Quantidade de itens: " + (result.getItens() != null ? result.getItens().size() : 0));
		return result;
	}

	@PostMapping("/search-in-rawg")
	public TotalItensBuscadosRawgDTO buscarRawg(@RequestParam String termo) {
		return this.rawgApi.principalMethod(termo);
	}

	@GetMapping("/get-prices")
	public List<MergerCheapSharkGamesAndStores> buscarPrecosCheapShark(@RequestParam String termo) {
		return this.cheapSharkApi.principalMethod(termo);
	}

}
