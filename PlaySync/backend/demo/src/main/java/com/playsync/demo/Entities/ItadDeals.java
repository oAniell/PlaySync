package com.playsync.demo.Entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "itad_deals")
@Getter
@Setter
@NoArgsConstructor
public class ItadDeals {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double price;
    private Double regular;
    private Double desconto;
    private Long shopId;
    private String shopName;
    @OneToMany(mappedBy = "itadDeals", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItadDrm> drms;
    @OneToMany(mappedBy = "itadDeals", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItadPlataforms> platforms;
    @ManyToOne
    @JoinColumn(name = "id_itad_main_class")
    private ItadMainClass itadMainClass;

    public ItadDeals(Double price, Double regular, Double desconto, Long shopId, String shopName) {
        this.price = price;
        this.regular = regular;
        this.desconto = desconto;
        this.shopId = shopId;
        this.shopName = shopName;
    }

}
