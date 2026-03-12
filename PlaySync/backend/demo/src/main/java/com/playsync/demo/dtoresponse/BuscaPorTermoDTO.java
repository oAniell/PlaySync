package com.playsync.demo.dtoresponse;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuscaPorTermoDTO {
	@JsonProperty("total")
	private Integer qtdDeItensEncontrados;
	@JsonProperty("items")
	private List<ItensFiltradosPeloTermoDTO> itens;

}
