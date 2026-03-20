package com.playsync.demo.service;

import org.springframework.stereotype.Service;

import com.playsync.demo.dtoresponse.TotalItensBuscadosRawgDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MergeServiceCheapSharkAndRawg {

    private final RawgApiService rawgApiService;

    private final CheapSharkApiService cheapSharkApiService;

    public void mergearRawgECheapShark(String termoEmComum) {
        TotalItensBuscadosRawgDTO totalItensBuscadosRawgDTO = this.rawgApiService.principalMethod(termoEmComum);
        
    }

}
