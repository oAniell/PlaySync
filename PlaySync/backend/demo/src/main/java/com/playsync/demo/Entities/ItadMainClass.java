package com.playsync.demo.Entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "itad_main_class")
@Getter
@Setter
@NoArgsConstructor
public class ItadMainClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id_game", unique = true)
    private String idGame;
    @OneToMany(mappedBy = "itadMainClass", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItadDeals> itad_deals = new ArrayList<>();
    @Column(name = "data_last_search")
    private LocalDateTime dataLastSearch;

    public ItadMainClass(String idGame, LocalDateTime dataLastSearch) {
        this.idGame = idGame;
        this.dataLastSearch = dataLastSearch;
    }

}
