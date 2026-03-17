package com.playsync.demo.Entities;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Collate;

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
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "rawg_api_busca_termo")
public class RawgApiBuscaTermo {
    

    /*
    id	Long	ID único do jogo na RAWG → serve para cache e relacionamentos
    name	String	Nome do jogo
    released	String / LocalDate	Data de lançamento
background_image	String	Imagem principal do jogo (para exibir)
rating	Double	Nota média do jogo
ratings_count	Integer	Número de avaliações (opcional)
platforms	List<String>	Nome das plataformas, ex.: ["PC", "PS4"]
genres	List<String>	Gêneros do jogo, ex.: ["Action", "Adventure"]
stores	List<String>	Lojas onde está disponível, ex.: ["Steam", "Epic"] */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;    
    @Column(name = "data_lancamento")
    private String dataLancamento;
    private String imgBackground;
    private String notaMediaJogo;
    private String numeroAvaliacoes;
    @OneToMany(mappedBy = "apiBuscaTermo")
    private List<PlataformasRawg>plataformas = new ArrayList<>();
    @OneToMany(mappedBy = "rawgApiBuscaTermo")
    private List<GenerosApiRawg>rawgApiBuscaTermo = new ArrayList<>();
    @OneToMany(mappedBy = "rawgApiBuscaTermo")
    private List<LojasRawgApi>rawgApiBusca = new ArrayList<>();


     
}   
