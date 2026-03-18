package com.playsync.demo.Entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Collate;

import jakarta.persistence.CascadeType;
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
@Table(name = "rawg_api_busca_termo")
public class RawgApiBuscaTermo {

    /*
     * id Long ID único do jogo na RAWG → serve para cache e relacionamentos
     * name String Nome do jogo
     * released String / LocalDate Data de lançamento
     * background_image String Imagem principal do jogo (para exibir)
     * rating Double Nota média do jogo
     * ratings_count Integer Número de avaliações (opcional)
     * platforms List<String> Nome das plataformas, ex.: ["PC", "PS4"]
     * genres List<String> Gêneros do jogo, ex.: ["Action", "Adventure"]
     * stores List<String> Lojas onde está disponível, ex.: ["Steam", "Epic"]
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    @Column(name = "data_lancamento")
    private String dataLancamento;
    private String imgBackground;
    private Double notaMediaJogo;
    private String numeroAvaliacoes;
    @Column(name = "id_game")
    private Long idGame;
    @ManyToOne
    @JoinColumn(name = "id_total_itens_buscados_rawg")
    private TotalItensBuscadosRawg totalItensBuscadosRawg;
    @OneToMany(mappedBy = "rawgApiBuscaTermo", cascade = CascadeType.ALL)
    private List<PlataformasRawgEntity> plataformasRawgs = new ArrayList<>();
    @OneToMany(mappedBy = "rawgApiBuscaTermo", cascade = CascadeType.ALL)
    private List<GenerosApiRawg> generosApiRawgs = new ArrayList<>();
    @OneToMany(mappedBy = "rawgApiBuscaTermo", cascade = CascadeType.ALL)
    private List<LojasRawgApi> lojasRawgApis = new ArrayList<>();
    @Column(name = "data_last_search")
    private LocalDateTime dataLastSearch;

    public RawgApiBuscaTermo(String nome, String dataLancamento, String imgBackground, Double notaMediaJogo,
            String numeroAvaliacoes, Long idGame, TotalItensBuscadosRawg totalItensBuscadosRawg,
            LocalDateTime dataLastSearch) {
        this.nome = nome;
        this.dataLancamento = dataLancamento;
        this.imgBackground = imgBackground;
        this.notaMediaJogo = notaMediaJogo;
        this.numeroAvaliacoes = numeroAvaliacoes;
        this.idGame = idGame;
        this.totalItensBuscadosRawg = totalItensBuscadosRawg;
        this.dataLastSearch = dataLastSearch;
    }

}
