import { useState, useCallback } from 'react';
import { mockGames, featuredGame, trendingGames, searchGames } from '../mockData';
import { gameService } from '../services/api';

// Hook para gerenciar dados de jogos
// TODO: Quando o backend estiver pronto, substituir os dados mock por chamadas à API
export const useGames = () => {
  const [games, setGames] = useState([]);
  const [featured, setFeatured] = useState(featuredGame);
  const [trending, setTrending] = useState(trendingGames);
  const [selectedGame, setSelectedGame] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  // Buscar jogos por termo
  const search = useCallback(async (term) => {
    setIsLoading(true);
    setError(null);

    try {
      // Chama a API real do backend Steam
      console.log('Buscando termo:', term);
      const results = await gameService.searchGames(term);
      console.log('Resultados recebidos:', results);
      console.log('Itens recebidos:', results.items);
      
      // Verifica se há itens na resposta
      if (!results.items || results.items.length === 0) {
        setError('Conteúdo não encontrado');
        setGames([]);
      } else {
        setGames(results.items || []);
      }
    } catch (err) {
      console.error('Erro na busca:', err);
      // Verifica se o erro é 404 (Conteúdo não encontrado)
      if (err.message.includes('404') || err.message.includes('Conteudo nao encontrado')) {
        setError('Conteúdo não encontrado');
      } else {
        setError(err.message);
      }
      setGames([]);
    } finally {
      setIsLoading(false);
    }
  }, []);

  // Carregar jogos em destaque
  const loadFeatured = useCallback(async () => {
    setIsLoading(true);
    
    try {
      // TODO: Substituir por API real
      // const data = await gameService.getFeaturedGames();
      // setFeatured(data);
      
      setFeatured(featuredGame);
    } catch (err) {
      setError(err.message);
    } finally {
      setIsLoading(false);
    }
  }, []);

  // Carregar jogos em tendência
  const loadTrending = useCallback(async () => {
    setIsLoading(true);
    
    try {
      // TODO: Substituir por API real
      // const data = await gameService.getTrendingGames();
      // setTrending(data);
      
      setTrending(trendingGames);
    } catch (err) {
      setError(err.message);
    } finally {
      setIsLoading(false);
    }
  }, []);

  // Selecionar um jogo para ver detalhes
  const selectGame = useCallback((gameId) => {
    const game = mockGames.find(g => g.id === gameId);
    setSelectedGame(game || null);
  }, []);

  // Limpar seleção
  const clearSelection = useCallback(() => {
    setSelectedGame(null);
  }, []);

  // Limpar resultados de busca
  const clearSearch = useCallback(() => {
    setGames([]);
  }, []);

  // Limpar erros
  const clearError = useCallback(() => {
    setError(null);
  }, []);

  return {
    games,
    featured,
    trending,
    selectedGame,
    isLoading,
    error,
    search,
    loadFeatured,
    loadTrending,
    selectGame,
    clearSelection,
    clearSearch,
    clearError,
  };
};

export default useGames;
