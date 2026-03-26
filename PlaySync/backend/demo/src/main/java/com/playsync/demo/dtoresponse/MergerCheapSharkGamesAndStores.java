package com.playsync.demo.dtoresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MergerCheapSharkGamesAndStores {

    private String nomeJogo;
    private Double precoAtual;
    private Double precoOriginal;
    private Double desconto;
    private Long storeId;
    private String nomeLoja;

}
