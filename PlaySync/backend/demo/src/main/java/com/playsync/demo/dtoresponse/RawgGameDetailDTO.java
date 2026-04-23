package com.playsync.demo.dtoresponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO específico para dados adicionais da RAWG API
 * Evita conflitos com a classe principal ItensFiltradosPeloTermoDTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RawgGameDetailDTO {
	private String nomeGeneros;
	private String nomePlataformas;
	private String dataLancamento;
	private Double avaliacao;
}
