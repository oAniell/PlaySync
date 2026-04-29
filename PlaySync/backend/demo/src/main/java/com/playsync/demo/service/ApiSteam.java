package com.playsync.demo.service;

import org.springframework.stereotype.Service;

import com.playsync.demo.dtoresponse.BuscaPorTermoDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ApiSteam {

    private final DynamoDbCacheService cacheService;

    public BuscaPorTermoDTO buscaPorTermo(String termo) {
        return cacheService.buscarPorTermo(termo);
    }
}
