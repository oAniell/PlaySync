package com.playsync.demo.controller;

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
public class BuscaPorTermoController {

	private final ApiSteam api;

	@PostMapping("/search")
	public BuscaPorTermoDTO buscar(@RequestParam String termo) {
		return this.api.buscaPorTermo(termo);
	}

}
