package com.playsync.demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.playsync.demo.dtoresponse.BuscaPorTermoDTO;
import com.playsync.demo.service.ApiSteam;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api-playsync")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class BuscaPorTermoController {

	private final ApiSteam api;

	@PostMapping("/search")
	public BuscaPorTermoDTO buscar(@RequestParam String termo) {
		System.out.println("Recebida requisicao de busca para termo: " + termo);
		BuscaPorTermoDTO result = this.api.buscaPorTermo(termo);
		System.out.println("Resultado retornado: " + result);
		System.out.println("Quantidade de itens: " + (result.getItens() != null ? result.getItens().size() : 0));
		return result;
	}

}
