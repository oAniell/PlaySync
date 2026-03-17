package com.playsync.demo.dtoresponse;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RawgApiBuscaTermoDTO {
    @JsonProperty("name")
    private String nome;    
    @JsonProperty("released")
    private String dataLancamento;
    @JsonProperty("background_image")
    private String imgBackground;
    @JsonProperty("rating")
    private Double notaMediaJogo;
    @JsonProperty("rating_top")
    private String numeroAvaliacoes;
    @JsonProperty("platforms")
    private List<PlataformasRawgDTO>plataformas = new ArrayList<>();
    @JsonProperty("genres")
    private List<GenerosApiRawgDTO>rawgApiBuscaTermo = new ArrayList<>();
    @JsonProperty("stores")
    private List<LojasRawgApiDTO>rawgApiBusca = new ArrayList<>();


     
}   
