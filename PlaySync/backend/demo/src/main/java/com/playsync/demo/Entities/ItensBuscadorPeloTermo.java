package com.playsync.demo.Entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.playsync.demo.dtoresponse.PrecoDeItensDTO;
import com.playsync.demo.enums.ControllerSupport;

import jakarta.annotation.Generated;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "tb_itens")
public class ItensBuscadorPeloTermo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "id_game")
	private Integer idGame;
	private String nome;
	@ManyToOne
	@JoinColumn(name = "id_busca_por_termo_lista")
	private BuscaPorTermo buscaPorTermo;
	@OneToMany(mappedBy = "preco", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PrecosJogos> precos = new ArrayList<>();
	private String img;
	@Column(name = "possui_compatibilidade_com_controle")
	@Enumerated(EnumType.STRING)
	private ControllerSupport possuiCompatibilidadeComControle;
	@Column(name = "data_pesquisa_usuario")
	private LocalDateTime dataPesquisaUsuario;

	public ItensBuscadorPeloTermo(Integer idGame, String nome, BuscaPorTermo buscaPorTermo, String img,
			ControllerSupport possuiCompatibilidadeComControle, LocalDateTime dataPesquisaUsuario) {
		super();
		this.idGame = idGame;
		this.nome = nome;
		this.buscaPorTermo = buscaPorTermo;
		this.img = img;
		this.possuiCompatibilidadeComControle = possuiCompatibilidadeComControle;
		this.dataPesquisaUsuario = dataPesquisaUsuario;
	}

	@Override
	public String toString() {
		return "ItensBuscadorPeloTermo [id=" + id + ", idGame=" + idGame + ", nome=" + nome + ", buscaPorTermo="
				+ buscaPorTermo + ", precos=" + precos + ", img=" + img + ", possuiCompatibilidadeComControle="
				+ possuiCompatibilidadeComControle + ", dataPesquisaUsuario=" + dataPesquisaUsuario + "]";
	}
	
	
	
}
