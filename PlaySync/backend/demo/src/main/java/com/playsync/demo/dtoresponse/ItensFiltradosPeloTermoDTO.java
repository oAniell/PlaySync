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
public class ItensFiltradosPeloTermoDTO {
	@JsonProperty("id")
	private Integer idGame;
	private String name;
	@JsonProperty("price")
	private PrecoDeItensDTO precos;
	@JsonProperty("tiny_image")
	private String img;
	@JsonProperty("controller_support")
	private String possuiCompatibilidadeComControle;

}
