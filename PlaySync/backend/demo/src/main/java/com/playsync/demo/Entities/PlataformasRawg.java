package com.playsync.demo.Entities;

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
@NoArgsConstructor
@Getter
@Setter
@Table(name = "plataformas_rawg")
public class PlataformasRawg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String plataforma;
    @ManyToOne
    @JoinColumn(name = "id_api_busca_termo")
    private RawgApiBuscaTermo apiBuscaTermo;


    public PlataformasRawg(String plataforma,RawgApiBuscaTermo apiBuscaTermo) {
        this.plataforma = plataforma;
        this.apiBuscaTermo = apiBuscaTermo;
    }

}
