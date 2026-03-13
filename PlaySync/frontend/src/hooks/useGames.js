import { useState, useCallback } from 'react';
import { mockGames } from '../mockData';
import { gameService } from '../services/api';

// Hook para gerenciar dados de jogos
// Os dados de featured e trending vêm da RAWG API
const emptyFeatured = {
  id: 0,
  title: '',
  coverImageUrl: '',
  backgroundImageUrl: '',
};

const emptyTrending = [];

export const useGames = () => {
  const [games, setGames] = useState([]);
  const [featured, setFeatured] = useState(emptyFeatured);
  const [trending, setTrending] = useState(emptyTrending);
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

  // Carregar jogos em destaque (da RAWG API)
  const loadFeatured = useCallback(async () => {
    setIsLoading(true);
    
    try {
      // Chama a API RAWG para buscar jogo em destaque
      const data = await gameService.getFeaturedGame();
      
      // Adapta os dados para o formato esperado pelo frontend
      if (data) {
        const adaptedFeatured = {
          id: data.idGame || data.id || 0,
          title: data.name || '',
          coverImageUrl: data.img || data.background_image || '',
          backgroundImageUrl: data.img || data.background_image || '',
          genres: data.nomeGeneros || '',
          rating: data.avaliacao || data.rating || 0,
          platforms: data.nomePlataformas || '',
        };
        setFeatured(adaptedFeatured);
      }
    } catch (err) {
      console.error('Erro ao carregar jogo em destaque:', err);
      setError(err.message);
    } finally {
      setIsLoading(false);
    }
  }, []);

  // Carregar jogos em tendência (da RAWG API)
  const loadTrending = useCallback(async () => {
    setIsLoading(true);
    
    try {
      // Chama a API RAWG para buscar jogos em tendência
      const data = await gameService.getTrendingGames(10);
      
      // Adapta os dados para o formato esperado pelo frontend
      if (data && Array.isArray(data)) {
        const adaptedTrending = data.map(game => ({
          id: game.idGame || game.id || 0,
          title: game.name || '',
          coverImageUrl: game.img || game.background_image || '',
          backgroundImageUrl: game.img || game.background_image || '',
          genres: game.nomeGeneros || '',
          rating: game.avaliacao || game.rating || 0,
          platforms: game.nomePlataformas || '',
        }));
        setTrending(adaptedTrending);
      }
    } catch (err) {
      console.error('Erro ao carregar jogos em tendência:', err);
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
