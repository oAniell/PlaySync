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
public class MergeListaDeLojaEPrecoResponse {

    private String nome_loja;
    private Double valor_atual;
    private Double valor_original;
    private Double desconto;

}
