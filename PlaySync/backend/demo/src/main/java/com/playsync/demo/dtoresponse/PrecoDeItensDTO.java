package com.playsync.demo.dtoresponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import jakarta.annotation.Generated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrecoDeItensDTO {
	@JsonProperty("initial")
	@JsonSetter(nulls = Nulls.SKIP)
	private Double precoInicial;
	@JsonProperty("final")
	@JsonSetter(nulls = Nulls.SKIP)
	private Double precoFinal;

}
