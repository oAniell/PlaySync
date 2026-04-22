package com.playsync.demo.dtoresponse;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MergerSearchAndPriceResponse {
    private String idGame;
    private String slug;
    private String nomeJogo;
    private String tipoDoItem;
    private ItadAssetsLista itadAssetsLista;
    private List<ItadDealsDto> deals;
}
