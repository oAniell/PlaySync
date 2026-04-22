package com.playsync.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.playsync.demo.dtoresponse.ItadBuscaPorTermoDto;
import com.playsync.demo.dtoresponse.ItadMainClassDto;
import com.playsync.demo.dtoresponse.MergerSearchAndPriceResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MergeSearchAndPriceService {
    private final ItadApiService itadApiService;
    private final ItadApiPrecosService itadApiPrecosService;

    public void principalMethod(String termoString) {
        List<ItadBuscaPorTermoDto> objects = this.itadApiService.principalMethod(termoString);
        List<String> ids = new ArrayList<>();
        for (ItadBuscaPorTermoDto itadBuscaPorTermoDto : objects) {
            ids.add(itadBuscaPorTermoDto.getIdGame());
        }
        List<ItadMainClassDto> itadMainClassDtos = this.itadApiPrecosService.principalMethod(ids);
    }

    private void auxMergerMethod(List<ItadMainClassDto> itadMainClassDtos, List<ItadBuscaPorTermoDto> objects) {
        List<MergerSearchAndPriceResponse> mergerSearchAndPriceResponses = new ArrayList<>();
        Map<String, ItadMainClassDto> mapperPrices = new HashMap<>();
        for (ItadMainClassDto itadMainClassDto : itadMainClassDtos) {
            mapperPrices.put(itadMainClassDto.getIdGame(), itadMainClassDto);
        }   
        
    }
}
