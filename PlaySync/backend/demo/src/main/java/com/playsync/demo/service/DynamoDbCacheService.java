package com.playsync.demo.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.playsync.demo.client.SteamClient;
import com.playsync.demo.dtoresponse.BuscaPorTermoDTO;
import com.playsync.demo.dtoresponse.ItensFiltradosPeloTermoDTO;
import com.playsync.demo.dtoresponse.PrecoDeItensDTO;
import com.playsync.demo.model.PlaySyncCacheItem;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

@Service
@Slf4j
@RequiredArgsConstructor
public class DynamoDbCacheService {

    private final DynamoDbEnhancedClient enhancedClient;
    private final SteamClient steamClient;

    private static final String TABLE_NAME = "playsync-cache";
    private static final long TTL_SECONDS = 21_600L;
    private static final String SK_META = "#meta";

    private DynamoDbTable<PlaySyncCacheItem> getTable() {
        return enhancedClient.table(TABLE_NAME, TableSchema.fromBean(PlaySyncCacheItem.class));
    }

    double converterPreco(int centavos) {
        return centavos / 100.0;
    }

    PlaySyncCacheItem buildItem(String termo, ItensFiltradosPeloTermoDTO jogo) {
        PlaySyncCacheItem item = new PlaySyncCacheItem();
        item.setTermoBusca(termo);
        item.setIdGame(String.valueOf(jogo.getIdGame()));
        item.setNome(jogo.getName());
        item.setImg(jogo.getImg() != null ? jogo.getImg() : "");
        item.setPrecoInicial(jogo.getPrecos() != null ? jogo.getPrecos().getPrecoInicial() : 0.0);
        item.setPrecoFinal(jogo.getPrecos() != null ? jogo.getPrecos().getPrecoFinal() : 0.0);
        item.setSuporteControle(jogo.getPossuiCompatibilidadeComControle() != null
                ? jogo.getPossuiCompatibilidadeComControle() : "NULL");
        item.setDataPesquisa(LocalDateTime.now().toString());
        item.setTtl(Instant.now().plusSeconds(TTL_SECONDS).getEpochSecond());
        return item;
    }

    private PlaySyncCacheItem buildMetaItem(String termo, int qtdItens) {
        PlaySyncCacheItem meta = new PlaySyncCacheItem();
        meta.setTermoBusca(termo);
        meta.setIdGame(SK_META);
        meta.setQtdItensEncontrados(qtdItens);
        meta.setTtl(Instant.now().plusSeconds(TTL_SECONDS).getEpochSecond());
        return meta;
    }

    ItensFiltradosPeloTermoDTO fromItem(PlaySyncCacheItem item) {
        ItensFiltradosPeloTermoDTO dto = new ItensFiltradosPeloTermoDTO();
        dto.setIdGame(Integer.parseInt(item.getIdGame()));
        dto.setName(item.getNome());
        dto.setImg(item.getImg());
        dto.setPossuiCompatibilidadeComControle(item.getSuporteControle());
        dto.setPrecos(new PrecoDeItensDTO(item.getPrecoInicial(), item.getPrecoFinal()));
        return dto;
    }

    private List<PlaySyncCacheItem> queryTermo(String termo) {
        try {
            return getTable()
                    .query(QueryConditional.keyEqualTo(Key.builder().partitionValue(termo).build()))
                    .items()
                    .stream()
                    .filter(i -> !SK_META.equals(i.getIdGame()))
                    .collect(Collectors.toList());
        } catch (DynamoDbException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Serviço de cache temporariamente indisponível", e);
        }
    }

    private void persistirItens(String termo, List<ItensFiltradosPeloTermoDTO> jogos) {
        try {
            DynamoDbTable<PlaySyncCacheItem> table = getTable();
            for (ItensFiltradosPeloTermoDTO jogo : jogos) {
                table.putItem(buildItem(termo, jogo));
            }
            table.putItem(buildMetaItem(termo, jogos.size()));
        } catch (DynamoDbException e) {
            log.error("Erro ao persistir no DynamoDB", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Serviço de cache temporariamente indisponível", e);
        }
    }

    public BuscaPorTermoDTO buscarPorTermo(String termo) {
        List<PlaySyncCacheItem> itens = queryTermo(termo);

        if (itens.isEmpty()) {
            // Cache miss
            BuscaPorTermoDTO buscaDto = steamClient.buscarPorTermo(termo).block();
            if (buscaDto == null || buscaDto.getItens() == null || buscaDto.getItens().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conteúdo não encontrado");
            }
            for (ItensFiltradosPeloTermoDTO item : buscaDto.getItens()) {
                if (item.getPrecos() == null) {
                    item.setPrecos(new PrecoDeItensDTO(0.0, 0.0));
                } else {
                    Double pi = item.getPrecos().getPrecoInicial();
                    Double pf = item.getPrecos().getPrecoFinal();
                    item.getPrecos().setPrecoInicial(pi != null ? pi / 100.0 : 0.0);
                    item.getPrecos().setPrecoFinal(pf != null ? pf / 100.0 : 0.0);
                }
                if (item.getPossuiCompatibilidadeComControle() == null) {
                    item.setPossuiCompatibilidadeComControle("NULL");
                }
            }
            persistirItens(termo, buscaDto.getItens());
            return buscaDto;
        }

        // Cache hit
        if (LocalDateTime.parse(itens.get(0).getDataPesquisa()).isAfter(LocalDateTime.now().minusHours(6))) {
            List<ItensFiltradosPeloTermoDTO> jogos = itens.stream()
                    .map(this::fromItem)
                    .collect(Collectors.toList());
            PlaySyncCacheItem meta = getTable().getItem(
                    Key.builder().partitionValue(termo).sortValue(SK_META).build());
            int qtd = meta != null && meta.getQtdItensEncontrados() != null
                    ? meta.getQtdItensEncontrados() : jogos.size();
            return new BuscaPorTermoDTO(qtd, jogos);
        }

        // Expirado — atualiza
        BuscaPorTermoDTO buscaDto = steamClient.buscarPorTermo(termo).block();
        if (buscaDto == null || buscaDto.getItens() == null || buscaDto.getItens().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conteúdo não encontrado");
        }
        DynamoDbTable<PlaySyncCacheItem> table = getTable();
        for (ItensFiltradosPeloTermoDTO item : buscaDto.getItens()) {
            if (item.getPrecos() == null) {
                item.setPrecos(new PrecoDeItensDTO(0.0, 0.0));
            } else {
                Double pi = item.getPrecos().getPrecoInicial();
                Double pf = item.getPrecos().getPrecoFinal();
                item.getPrecos().setPrecoInicial(pi != null ? pi / 100.0 : 0.0);
                item.getPrecos().setPrecoFinal(pf != null ? pf / 100.0 : 0.0);
            }
            if (item.getPossuiCompatibilidadeComControle() == null) {
                item.setPossuiCompatibilidadeComControle("NULL");
            }
            table.putItem(buildItem(termo, item));
        }
        table.putItem(buildMetaItem(termo, buscaDto.getItens().size()));
        return buscaDto;
    }

    public List<String> findMostSearchedGameNames(LocalDateTime startDate, int limit) {
        try {
            String startDateStr = startDate.toString();
            Map<String, AttributeValue> expressionValues = Map.of(
                    ":startDate", AttributeValue.builder().s(startDateStr).build(),
                    ":meta", AttributeValue.builder().s(SK_META).build());

            ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                    .filterExpression(software.amazon.awssdk.enhanced.dynamodb.Expression.builder()
                            .expression("dataPesquisa >= :startDate AND idGame <> :meta")
                            .expressionValues(expressionValues)
                            .build())
                    .build();

            List<PlaySyncCacheItem> items = new ArrayList<>();
            getTable().scan(request).items().forEach(items::add);

            return items.stream()
                    .collect(Collectors.groupingBy(PlaySyncCacheItem::getNome, Collectors.counting()))
                    .entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                    .limit(limit)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        } catch (DynamoDbException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Serviço de cache temporariamente indisponível", e);
        }
    }
}
