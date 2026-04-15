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
@Table(name = "itad_regular")
@Getter
@Setter
@NoArgsConstructor
public class ItadRegular {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double quantia;
    @ManyToOne
    @JoinColumn(name = "id_itad_deals")
    private ItadDeals itadDeals;
    public ItadRegular(Double quantia, ItadDeals itadDeals) {
        this.quantia = quantia;
        this.itadDeals = itadDeals;
    }
}
