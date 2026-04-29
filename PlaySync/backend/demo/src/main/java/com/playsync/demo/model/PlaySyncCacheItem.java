package com.playsync.demo.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
public class PlaySyncCacheItem {

    @Getter(AccessLevel.NONE)
    private String termoBusca;

    @Getter(AccessLevel.NONE)
    private String idGame;

    private String nome;
    private String img;
    private Double precoInicial;
    private Double precoFinal;
    private String suporteControle;
    private String dataPesquisa;
    private Long ttl;
    private Integer qtdItensEncontrados;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("termoBusca")
    public String getTermoBusca() {
        return termoBusca;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("idGame")
    public String getIdGame() {
        return idGame;
    }
}
