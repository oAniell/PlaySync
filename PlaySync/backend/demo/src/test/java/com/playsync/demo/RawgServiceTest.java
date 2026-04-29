package com.playsync.demo;

import com.playsync.demo.client.RawgClient;
import com.playsync.demo.dtoresponse.ItensFiltradosPeloTermoDTO;
import com.playsync.demo.dtoresponse.RawgGame;
import com.playsync.demo.dtoresponse.RawgGameResponse;
import com.playsync.demo.service.DynamoDbCacheService;
import com.playsync.demo.service.RawgService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RawgServiceTest {

    @Mock
    private RawgClient rawgClient;

    @Mock
    private DynamoDbCacheService cacheService;

    @InjectMocks
    private RawgService rawgService;

    @Test
    void getFeaturedGame_usaFallbackRawgQuandoCacheVazio() {
        when(cacheService.findMostSearchedGameNames(any(LocalDateTime.class), anyInt()))
                .thenReturn(List.of());

        RawgGame game = new RawgGame();
        game.setId(1L);
        game.setName("Trending Game");
        RawgGameResponse response = new RawgGameResponse();
        response.setResults(List.of(game));
        when(rawgClient.getTrendingGames(anyInt())).thenReturn(Mono.just(response));

        Mono<ItensFiltradosPeloTermoDTO> result = rawgService.getFeaturedGame();

        assertThat(result.block()).isNotNull();
    }

    @Test
    void getFeaturedGame_semExcecaoQuandoCacheVazio() {
        when(cacheService.findMostSearchedGameNames(any(LocalDateTime.class), anyInt()))
                .thenReturn(List.of());

        RawgGameResponse response = new RawgGameResponse();
        response.setResults(List.of());
        when(rawgClient.getTrendingGames(anyInt())).thenReturn(Mono.just(response));

        assertThatNoException().isThrownBy(() -> rawgService.getFeaturedGame().block());
    }
}
