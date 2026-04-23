package com.playsync.demo.dtoresponse;

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
	
	// Dados adicionais da RAWG API
	private RawgGameDetailDTO rawgDetails;

	// Screenshots do jogo (URLs das imagens)
	private List<String> screenshots;

	// Capsule da Steam com logo do jogo (ex: header.jpg) — para cards de trending
	private String steamCapsuleUrl;
}
