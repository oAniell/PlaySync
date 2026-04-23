package com.playsync.demo.dtoresponse;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItadBuscaPorTermoDto {
    @JsonProperty("id")
    private String idGame;
    private String slug;
    @JsonProperty("title")
    private String nomeJogo;
    @JsonProperty("type")
    private String tipoDoItem;
    @JsonProperty("assets")
    private ItadAssetsLista itadAssetsLista;
}