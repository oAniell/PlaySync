package com.playsync.demo.Entities;

import org.hibernate.annotations.ManyToAny;

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
@Table(name = "itad_plataformas")
@Getter
@Setter
@NoArgsConstructor
public class ItadPlataforms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id_plataforma")
    private Long idPlataforma;
    @Column(name = "nome_plataforma")
    private String nomePlataforma;
    @ManyToOne
    @JoinColumn(name = "id_itad_deals")
    private ItadDeals itadDeals;
    public ItadPlataforms(Long idPlataforma, String nomePlataforma, ItadDeals itadDeals) {
        this.idPlataforma = idPlataforma;
        this.nomePlataforma = nomePlataforma;
        this.itadDeals = itadDeals;
    }

    

}
