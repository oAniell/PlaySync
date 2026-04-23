package com.playsync.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.playsync.demo.Entities.ItadMainClass;
import com.playsync.demo.dtoresponse.ItadBuscaPorTermoDto;
import com.playsync.demo.dtoresponse.ItadMainClassDto;
import com.playsync.demo.dtoresponse.MergerSearchAndPriceResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MergeSearchAndPriceService {
    private final ItadApiService itadApiService;
    private final ItadApiPrecosService itadApiPrecosService;

    public List<MergerSearchAndPriceResponse> principalMethod(String termoString) {
        List<ItadBuscaPorTermoDto> objects = this.itadApiService.principalMethod(termoString);
        List<String> ids = new ArrayList<>();
        for (ItadBuscaPorTermoDto itadBuscaPorTermoDto : objects) {
            ids.add(itadBuscaPorTermoDto.getIdGame());
        }
        List<ItadMainClassDto> itadMainClassDtos = this.itadApiPrecosService.principalMethod(ids);
        return auxMergerMethod(itadMainClassDtos, objects);
    }

    private List<MergerSearchAndPriceResponse> auxMergerMethod(List<ItadMainClassDto> itadMainClassDtos,
            List<ItadBuscaPorTermoDto> objects) {
        List<MergerSearchAndPriceResponse> mergerSearchAndPriceResponses = new ArrayList<>();
        Map<String, ItadMainClassDto> mapperPrices = new HashMap<>();
        for (ItadMainClassDto itadMainClassDto : itadMainClassDtos) {
            mapperPrices.put(itadMainClassDto.getIdGame(), itadMainClassDto);
        }
        for (ItadBuscaPorTermoDto itadBuscaPorTermoDto : objects) {
            ItadMainClassDto itadMainClassDto = mapperPrices.get(itadBuscaPorTermoDto.getIdGame());
            if (itadMainClassDto != null
                    && itadMainClassDto.getDeals() != null
                    && !itadMainClassDto.getDeals().isEmpty()) {

                MergerSearchAndPriceResponse mergerSearchAndPriceResponse = new MergerSearchAndPriceResponse();
                validadorDeAtributosPrices(itadMainClassDto, mergerSearchAndPriceResponse, itadBuscaPorTermoDto);
                mergerSearchAndPriceResponses.add(mergerSearchAndPriceResponse);
            }
        }
        return mergerSearchAndPriceResponses;
    }

    private void validadorDeAtributosPrices(ItadMainClassDto itadMainClass,
            MergerSearchAndPriceResponse mergerSearchAndPriceResponse, ItadBuscaPorTermoDto itadBuscaPorTermoDto) {
        if (itadMainClass != null && itadMainClass.getIdGame() != null && itadMainClass.getDeals() != null
                && !itadMainClass.getDeals().isEmpty()) {

            mergerSearchAndPriceResponse.setDeals(itadMainClass.getDeals());
            mergerSearchAndPriceResponse.setIdGame(itadMainClass.getIdGame());
            if (itadBuscaPorTermoDto.getNomeJogo() != null) {
                mergerSearchAndPriceResponse.setNomeJogo(itadBuscaPorTermoDto.getNomeJogo());
            }
            if (itadBuscaPorTermoDto.getSlug() != null) {
                mergerSearchAndPriceResponse.setSlug(itadBuscaPorTermoDto.getSlug());
            }
            if (itadBuscaPorTermoDto.getTipoDoItem() != null) {
                mergerSearchAndPriceResponse.setTipoDoItem(itadBuscaPorTermoDto.getTipoDoItem());
            }
            validaAssets(itadBuscaPorTermoDto, mergerSearchAndPriceResponse);

        }
    }

    private void validaAssets(ItadBuscaPorTermoDto itadBuscaPorTermoDto,
            MergerSearchAndPriceResponse mergerSearchAndPriceResponse) {
        if (itadBuscaPorTermoDto.getItadAssetsLista() != null
                && itadBuscaPorTermoDto.getItadAssetsLista().getImagem01() != null
                && itadBuscaPorTermoDto.getItadAssetsLista().getArteSecundaria() != null) {
            mergerSearchAndPriceResponse.setItadAssetsLista(itadBuscaPorTermoDto.getItadAssetsLista());
        }
    }
}
