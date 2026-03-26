package com.playsync.demo.dtoresponse;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class RawgApiBuscaTermoDTO {
    @JsonProperty("name")
    private String nome;
    @JsonProperty("released")
    private String dataLancamento;
    @JsonProperty("background_image")
    private String imgBackground;
    @JsonProperty("id")
    private Long idGame;
    @JsonProperty("rating")
    private Double notaMediaJogo;
    @JsonProperty("rating_top")
    private String numeroAvaliacoes;
    @JsonProperty("platforms")
    private List<PlataformsRawg> plataformas = new ArrayList<>();
    @JsonProperty("genres")
    private List<GenerosApiRawgDTO> generosApiRawgDTOs = new ArrayList<>();
    @JsonProperty("stores")
    private List<StoresRawg> storesRawgs = new ArrayList<>();
    /*
     * realizamos o mapeamento de maneira que pegassemos apenas aqueles campos que
     * se tornam relevantes tanto para o ususario que vai receber a info no front,
     * quanto para o banco de dados
     */
}
