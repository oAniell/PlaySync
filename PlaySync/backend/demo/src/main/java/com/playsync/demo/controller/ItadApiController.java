package com.playsync.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.playsync.demo.client.PriceClientItad;
import com.playsync.demo.dtoresponse.ItadBuscaPorTermoDto;
import com.playsync.demo.dtoresponse.ItadMainClassDto;
import com.playsync.demo.service.ItadApiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api-playsync/v2")
public class ItadApiController {
    private final ItadApiService itadApiService;
    private final PriceClientItad priceClientItad;

    @GetMapping("/search/")
    public List<ItadBuscaPorTermoDto> buscaPorTermo(@RequestParam String termo) {
        return null;
    }
    @GetMapping("/prices")
    public List<ItadMainClassDto> buscarPorIds() {
        List<String> ids = new ArrayList<>();
        ids.add("018d937f-3a3b-7210-bd2d-0d1dfb1d84c0");
        return priceClientItad.buscarPrecos(ids).block();
    }

}
