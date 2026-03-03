package com.playsync.demo.dtoresponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BuscaPorTermoDTO {

	private Integer qtdDeItensEncontrados;

	private ItensFiltradosPeloTermoDTO itens;

}
