package com.playsync.demo.Entities;

import java.util.ArrayList;
import java.util.List;

import com.playsync.demo.dtoresponse.ItensFiltradosPeloTermoDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_search")
public class BuscaPorTermo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "qtd_itens_encontrados")
	private Integer qtdDeItensEncontrados;
	@OneToMany(mappedBy = "buscaPorTermo")
	private List<ItensBuscadorPeloTermo> itens = new ArrayList<>();
	public BuscaPorTermo(Integer qtdDeItensEncontrados) {
		this.qtdDeItensEncontrados = qtdDeItensEncontrados;
	}

}
