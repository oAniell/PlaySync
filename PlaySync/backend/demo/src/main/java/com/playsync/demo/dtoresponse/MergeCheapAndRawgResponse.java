package com.playsync.demo.dtoresponse;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MergeCheapAndRawgResponse {

    private String nome_jogo;
    private String data_lancamento;
    private String img_background;
    private Long id_Game;
    private Double nota_media_jogo;
    private String numero_avaliacoes;
    private List<MergeListaDeLojaEPrecoResponse> lojas_e_seus_valores;
    private List<PlataformasDisponiveisViaRawgParaMerge> plataformas;
    private List<MergeGenerosRawgAndCheapShark> generos = new ArrayList<>();

}
