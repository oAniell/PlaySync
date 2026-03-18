package com.playsync.demo.Entities;

import org.hibernate.annotations.Collate;

import jakarta.persistence.Column;
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
@Table(name = "lojas_rawg_api")
@Getter
@Setter
@NoArgsConstructor
public class LojasRawgApi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    @Column(name = "id_loja")
    private Long idLoja;
    @ManyToOne
    @JoinColumn(name = "id_rawg_api_busca_termo")
    private RawgApiBuscaTermo rawgApiBuscaTermo;

    public LojasRawgApi(String nome, Long idLoja) {
        this.nome = nome;
        this.idLoja = idLoja;
    }

}
