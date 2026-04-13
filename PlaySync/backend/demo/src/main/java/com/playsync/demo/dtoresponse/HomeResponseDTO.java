package com.playsync.demo.dtoresponse;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HomeResponseDTO {
	private ItensFiltradosPeloTermoDTO featured;
	private List<ItensFiltradosPeloTermoDTO> trending;
}
