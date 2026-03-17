package com.playsync.demo.Entities;

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
@Getter
@Setter
@NoArgsConstructor
@Table(name = "generos_api_rawg")
public class GenerosApiRawg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    @ManyToOne
    @JoinColumn(name = "id_rawg_api_busca_termo")
    private RawgApiBuscaTermo rawgApiBuscaTermo;
    public GenerosApiRawg(String nome, RawgApiBuscaTermo rawgApiBuscaTermo) {
        this.nome = nome;
        this.rawgApiBuscaTermo = rawgApiBuscaTermo;
    }

    

}
