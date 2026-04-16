package com.playsync.demo.Entities;

import java.util.ArrayList;
import java.util.List;

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
    @ManyToOne
    @JoinColumn(name = "id_itad_main_class")
    private ItadMainClass itadMainClass;
    @OneToMany(mappedBy = "itadDeals")
    private List<ItadShop> itadShops = new ArrayList<>();
    @OneToMany(mappedBy = "itadDeals")
    private List<ItadPrice> itadPrices = new ArrayList<>();
    @OneToMany(mappedBy = "itadDeals")
    private List<ItadRegular> itadRegulars = new ArrayList<>();
    private Double desconto;
    @OneToMany(mappedBy = "itadDeals")
    private List<ItadDrm> itadDrms = new ArrayList<>();
    @OneToMany(mappedBy = "itadDeals")
    private List<ItadPlataforms> itadPlataforms = new ArrayList<>();

    public ItadDeals(ItadMainClass itadMainClass, Double desconto) {
        this.itadMainClass = itadMainClass;
        this.desconto = desconto;
    }

}
