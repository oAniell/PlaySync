package com.playsync.demo.dtoresponse;

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
public class InformacoesDolarParaReal {
    @JsonProperty("code")
    private String codigo_Converter;
    @JsonProperty("codein")
    private String codigo_Convertido;
    @JsonProperty("bid")
    private Double valor_dolar;
}
