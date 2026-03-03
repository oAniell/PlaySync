package com.playsync.demo.dtoresponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItensFiltradosPeloTermoDTO {

	private Integer idGame;
	private String name;
	private PrecoDeItensDTO precos;
	private String img;
	private boolean possuiCompatibilidadeComControle;

}
