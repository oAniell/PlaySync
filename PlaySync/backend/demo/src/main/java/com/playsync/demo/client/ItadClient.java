package com.playsync.demo.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.playsync.demo.dtoresponse.ItadBuscaPorTermoDto;

import reactor.core.publisher.Mono;

@Service
public class ItadClient {

    

    /*
     * Como funciona na prática
     * 1️ Você precisa do ID do jogo (não o nome)
     * 
     * Passe primeiro pelo endpoint de busca de jogo para pegar o ID:
     * 
     * Buscar jogo por termo (GET)
     * GET
     * https://api.isthereanydeal.com/games/search/v1?key=SEU_API_KEY&title=elden%
     * 20ring -- usuario passará aqui o termo do jogo
     * retornara varios objetos que contem a tag ID que passaremos no price
     * 
     * Isso te retorna um array de jogos com os respectivos IDs.
     * 
     * 2️Depois usa o ID para chamar preços
     * Exemplo de requisição POST para preços
     * 
     * Endpoint:
     * 
     * POST https://api.isthereanydeal.com/games/prices/v3
     * 
     * Headers obrigatórios:
     * Content-Type: application/json
     * Body (JSON):
     * [
     * "018d937f-07fc-72ed-8517-d8e24cb1eb22"
     * ]
     */

    /*
     * Esse array deve conter IDs retornados pelo search/lookup.
     * 
     * Então a URL fica:
     * POST
     * https://api.isthereanydeal.com/games/prices/v3?key=SEU_API_KEY&country=BR
     * api key = baf038a8b8a8039c1e5f0def483913f055640f6e
     */
    private final WebClient webClient;

    public ItadClient(@Qualifier("buscaPorTermoITADCliente") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<List<ItadBuscaPorTermoDto>> buscarPorTermo(String termo) {
        return this.webClient.get().uri(uri -> uri.path("/search/v1")
                .queryParam("title", termo)
                .queryParam("key", "baf038a8b8a8039c1e5f0def483913f055640f6e").build()).retrieve()
                .bodyToFlux(ItadBuscaPorTermoDto.class).collectList();
    }




}
