package com.playsync.demo.Entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
@Table(name = "cheap_shark_jogos_precos")
@NoArgsConstructor
@Getter
@Setter
public class CheapSharkJogosEPrecosApi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nome_jogo")
    private String nomeJogo;
    @Column(name = "preco_atual")
    private Double precoAtual;
    @Column(name = "preco_original")
    private Double precoOriginal;
    @Column(name = "desconto")
    private Double desconto;
    @Column(name = "store_id")
    private Long storeId;
    @ManyToOne
    @JoinColumn(name = "id_loja_jogo")
    private CheapSharkLojasApi cheapSharkLojasApi;

    public CheapSharkJogosEPrecosApi(String nomeJogo, Double precoAtual, Double precoOriginal, Double desconto,
            Long storeId, CheapSharkLojasApi cheapSharkLojasApi) {
        this.nomeJogo = nomeJogo;
        this.precoAtual = precoAtual;
        this.precoOriginal = precoOriginal;
        this.desconto = desconto;
        this.storeId = storeId;
        this.cheapSharkLojasApi = cheapSharkLojasApi;
    }

}
