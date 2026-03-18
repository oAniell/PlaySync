package com.playsync.demo.Entities;

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
@NoArgsConstructor
@Getter
@Setter
@Table(name = "plataformas_rawg")
public class PlataformasRawgEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String plataforma;
    @Column(name = "id_plataforma")
    private Long idPlataforma;
    @ManyToOne
    @JoinColumn(name = "id_api_busca_termo")
    private RawgApiBuscaTermo rawgApiBuscaTermo;

    public PlataformasRawgEntity(String plataforma,Long idPlataforma) {
        this.plataforma = plataforma;
        this.idPlataforma= idPlataforma;
    }

}
