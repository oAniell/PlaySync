package com.playsync.demo;

import com.playsync.demo.client.SteamClient;
import com.playsync.demo.dtoresponse.BuscaPorTermoDTO;
import com.playsync.demo.dtoresponse.ItensFiltradosPeloTermoDTO;
import com.playsync.demo.dtoresponse.PrecoDeItensDTO;
import com.playsync.demo.model.PlaySyncCacheItem;
import com.playsync.demo.service.DynamoDbCacheService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;

import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class DynamoDbCacheServiceTest {

    @Mock
    private DynamoDbEnhancedClient enhancedClient;

    @Mock
    private SteamClient steamClient;

    @Mock
    private DynamoDbTable<PlaySyncCacheItem> table;

    @Mock
    private PageIterable<PlaySyncCacheItem> pageIterable;

    private DynamoDbCacheService service;

    @BeforeEach
    void setUp() {
        service = new DynamoDbCacheService(enhancedClient, steamClient);
        when(enhancedClient.table(anyString(), any(TableSchema.class))).thenReturn(table);
    }

    private SdkIterable<PlaySyncCacheItem> iterableOf(List<PlaySyncCacheItem> items) {
        return () -> items.iterator();
    }

    @Test
    void cacheMiss_chamaApiEPersiste() {
        when(table.query(any(QueryConditional.class))).thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(iterableOf(List.of()));

        ItensFiltradosPeloTermoDTO jogo = new ItensFiltradosPeloTermoDTO(
                1, "Test Game", new PrecoDeItensDTO(10000.0, 8000.0), "img.jpg", "FULL", null, null, null);
        BuscaPorTermoDTO dto = new BuscaPorTermoDTO(1, List.of(jogo));
        when(steamClient.buscarPorTermo(any())).thenReturn(Mono.just(dto));

        BuscaPorTermoDTO result = service.buscarPorTermo("test");

        assertThat(result).isNotNull();
        verify(steamClient).buscarPorTermo("test");
        // N jogos + 1 meta = 2 chamadas putItem
        verify(table, times(2)).putItem(any(PlaySyncCacheItem.class));
    }

    @Test
    void cacheHit_naoInvocaApi() {
        PlaySyncCacheItem cachedItem = new PlaySyncCacheItem();
        cachedItem.setTermoBusca("test");
        cachedItem.setIdGame("1");
        cachedItem.setNome("Test Game");
        cachedItem.setImg("img.jpg");
        cachedItem.setSuporteControle("FULL");
        cachedItem.setPrecoInicial(100.0);
        cachedItem.setPrecoFinal(80.0);
        cachedItem.setDataPesquisa(LocalDateTime.now().minusHours(1).toString());

        when(table.query(any(QueryConditional.class))).thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(iterableOf(List.of(cachedItem)));
        when(table.getItem(any(Key.class))).thenReturn(null);

        BuscaPorTermoDTO result = service.buscarPorTermo("test");

        assertThat(result).isNotNull();
        verify(steamClient, never()).buscarPorTermo(any());
    }

    @Test
    void apiVazia_lanca404() {
        when(table.query(any(QueryConditional.class))).thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(iterableOf(List.of()));
        when(steamClient.buscarPorTermo(any())).thenReturn(Mono.just(new BuscaPorTermoDTO(0, List.of())));

        assertThatThrownBy(() -> service.buscarPorTermo("vazio"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode().value()).isEqualTo(404));
    }

    @Test
    void dynamoDbExceptionEmLeitura_lanca503() {
        when(table.query(any(QueryConditional.class))).thenThrow(DynamoDbException.builder().message("erro").build());

        assertThatThrownBy(() -> service.buscarPorTermo("erro"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode().value()).isEqualTo(503));
    }

    @Test
    void dynamoDbExceptionEmEscrita_lanca503() {
        when(table.query(any(QueryConditional.class))).thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(iterableOf(List.of()));

        ItensFiltradosPeloTermoDTO jogo = new ItensFiltradosPeloTermoDTO(
                1, "Game", new PrecoDeItensDTO(100.0, 80.0), "img.jpg", "FULL", null, null, null);
        when(steamClient.buscarPorTermo(any())).thenReturn(Mono.just(new BuscaPorTermoDTO(1, List.of(jogo))));
        doThrow(DynamoDbException.builder().message("write error").build())
                .when(table).putItem(any(PlaySyncCacheItem.class));

        assertThatThrownBy(() -> service.buscarPorTermo("erro"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode().value()).isEqualTo(503));
    }
}
