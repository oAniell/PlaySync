package com.playsync.demo.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
}
