package com.playsync.demo.Entities;

import org.hibernate.annotations.Collate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "itad_precos_games")
@Getter
@Setter
@NoArgsConstructor
public class ItadPrecosGames {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id_game")
    private Long idGame;
    @Column(name = "preco_jogo_atualmente")
    private Double precoJogoAtualmente;
    @Column(name = "preco_jogo_bruto")
    private Double precoJogoBruto;
    @Column(name = "percentual_desconto")
    private Double percentualDesconto;
    
    /*
     * id → identificador único do jogo (base de tudo)
     * price.amount → preço atual da oferta
     * regular.amount → preço original (sem desconto)
     * cut → percentual de desconto
     * shop.name → nome da loja que está vendendo
     * historyLow.all.amount → menor preço da história (referência principal)
     * historyLow.m3.amount → menor preço recente (tendência)
     * expiry → quando a promoção termina
     * drm → plataforma onde o jogo será ativado (ex: Steam)
     * platforms → sistemas compatíveis (ex: Windows)
     */

}
