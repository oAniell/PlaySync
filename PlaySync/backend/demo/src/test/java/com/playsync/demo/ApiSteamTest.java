package com.playsync.demo;

import com.playsync.demo.dtoresponse.BuscaPorTermoDTO;
import com.playsync.demo.dtoresponse.ItensFiltradosPeloTermoDTO;
import com.playsync.demo.service.ApiSteam;
import com.playsync.demo.service.DynamoDbCacheService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiSteamTest {

    @Mock
    private DynamoDbCacheService cacheService;

    @InjectMocks
    private ApiSteam apiSteam;

    @Test
    void buscaPorTermo_delegaParaCacheService() {
        BuscaPorTermoDTO expected = new BuscaPorTermoDTO(1, List.of(new ItensFiltradosPeloTermoDTO()));
        when(cacheService.buscarPorTermo("elden ring")).thenReturn(expected);

        BuscaPorTermoDTO result = apiSteam.buscaPorTermo("elden ring");

        verify(cacheService).buscarPorTermo("elden ring");
        assertThat(result).isSameAs(expected);
    }

    @Test
    void buscaPorTermo_propagaRetornoSemModificacao() {
        BuscaPorTermoDTO expected = new BuscaPorTermoDTO(2, List.of());
        when(cacheService.buscarPorTermo("mario")).thenReturn(expected);

        BuscaPorTermoDTO result = apiSteam.buscaPorTermo("mario");

        assertThat(result.getQtdDeItensEncontrados()).isEqualTo(2);
        assertThat(result).isSameAs(expected);
    }
}
