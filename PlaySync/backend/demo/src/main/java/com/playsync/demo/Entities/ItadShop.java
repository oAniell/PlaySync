package com.playsync.demo.Entities;

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
@Table(name = "itad_shop")
@Getter
@Setter
@NoArgsConstructor
public class ItadShop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id_shop")
    private Long idLoja;
    private String nome;
    @ManyToOne
    @JoinColumn(name = "id_itad_deals")
    private ItadDeals itadDeals;

    public ItadShop(Long idLoja, String nome, ItadDeals itadDeals) {
        this.idLoja = idLoja;
        this.nome = nome;
        this.itadDeals = itadDeals;
    }



}
