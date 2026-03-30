package com.playsync.demo.Entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "itad_assets_de_itens")
@Getter
@Setter
@NoArgsConstructor
public class ItadAssetsDeItens {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String imagem01;
    private String arteSecundaria;
    @ManyToOne
    @JoinColumn(name = "id_itad_busca_por_termo")
    private ItadBuscaPorTermo itadBuscaPorTermo;
    private LocalDateTime dataLastSearch;

    public ItadAssetsDeItens(String imagem01, String arteSecundaria, ItadBuscaPorTermo itadBuscaPorTermo,
            LocalDateTime dataLastSearch) {
        this.imagem01 = imagem01;
        this.arteSecundaria = arteSecundaria;
        this.itadBuscaPorTermo = itadBuscaPorTermo;
        this.dataLastSearch = dataLastSearch;
    }

}
