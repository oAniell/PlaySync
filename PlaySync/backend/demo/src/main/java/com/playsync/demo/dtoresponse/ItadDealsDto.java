package com.playsync.demo.dtoresponse;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItadDealsDto {
    /* SHOPDTO PARA EU CRIAR POSTERIORMENTE */
    private String shop;
    /*PriceDTO Entidade para criar posteriomente */
    private String price;
    /*Preco regular/preco sem desconto nem nada */
    private String regular;
    private Double desconto;
    /*Lista de Drm */
    private String drm;
    private List<String> plataformas;
    
    
}
