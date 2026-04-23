package com.playsync.demo.Entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.playsync.demo.dtoresponse.ItadAssetsLista;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "itad_busca_por_termo")
@Getter
@Setter
@NoArgsConstructor
public class ItadBuscaPorTermo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id_game")
    private String idGame;
    private String slug;
    @Column(name = "nome_jogo")
    private String nomeJogo;
    @Column(name = "tipo_do_item")
    private String tipoDoItem;
    @Column(name = "data_last_search")
    private LocalDateTime dataLastSearch;
    @OneToOne(mappedBy = "itadBuscaPorTermo", cascade = CascadeType.ALL, orphanRemoval = true)
    private ItadAssetsDeItens assetsItens;

    public ItadBuscaPorTermo(String idGame, String slug, String nomeJogo, String tipoDoItem,
            LocalDateTime dataLastSearch, ItadAssetsDeItens assetsDeItens) {
        this.idGame = idGame;
        this.slug = slug;
        this.nomeJogo = nomeJogo;
        this.tipoDoItem = tipoDoItem;
        this.dataLastSearch = dataLastSearch;
        this.assetsItens = assetsDeItens;
    }

}
