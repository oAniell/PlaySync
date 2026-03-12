package com.playsync.demo.Entities;

import java.util.List;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_precos_jogos")
public class PrecosJogos {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "preco_inicial")
	private Double precoInicial;
	@Column(name = "preco_final")
	private Double precoFinal;
	@ManyToOne
	@JoinColumn(name = "id_item_buscado_por_termo")
	private ItensBuscadorPeloTermo preco;
	public PrecosJogos(Double precoInicial, Double precoFinal, ItensBuscadorPeloTermo preco) {
		super();
		this.precoInicial = precoInicial;
		this.precoFinal = precoFinal;
		this.preco = preco;
	}

	

}
