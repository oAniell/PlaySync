package com.playsync.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.playsync.demo.dtoresponse.ItadBuscaPorTermoDto;
import com.playsync.demo.service.ItadApiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api-playsync/v2")
public class ItadApiController {
    private final ItadApiService itadApiService;
    
    @GetMapping("/search/")
    public List<ItadBuscaPorTermoDto> buscaPorTermo(@RequestParam String termo) {
        return null;
    }
}
