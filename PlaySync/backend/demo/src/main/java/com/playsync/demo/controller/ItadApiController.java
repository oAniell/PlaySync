package com.playsync.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.playsync.demo.client.PriceClientItad;
import com.playsync.demo.dtoresponse.ItadBuscaPorTermoDto;
import com.playsync.demo.dtoresponse.ItadMainClassDto;
import com.playsync.demo.dtoresponse.MergerSearchAndPriceResponse;
import com.playsync.demo.service.ItadApiService;
import com.playsync.demo.service.MergeSearchAndPriceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api-playsync/v2")
public class ItadApiController {
    private final ItadApiService itadApiService;
    private final PriceClientItad priceClientItad;
    private final MergeSearchAndPriceService mergeSearchAndPriceService;

    @PostMapping("/search")
    public List<MergerSearchAndPriceResponse> mergeDeInfosLoco(@RequestParam String termoString) {
        return this.mergeSearchAndPriceService.principalMethod(termoString);
    }

}
