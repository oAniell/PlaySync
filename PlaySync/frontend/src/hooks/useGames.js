import { useState, useCallback } from 'react';
import { gameService } from '../services/api';

// URL fallback para imagens quebradas
const FALLBACK_IMAGE = 'https://images.unsplash.com/photo-1612287230217-8c7684717995?w=400&h=300&fit=crop';

// Função para normalizar URLs de imagens (Steam retorna URLs relativas)
const normalizeImageUrl = (url) => {
  if (!url) return FALLBACK_IMAGE;
  // Se a URL começa com //, adicionar https:
  if (url.startsWith('//')) {
    return 'https:' + url;
  }
  return url;
};

// Hook para gerenciar dados de jogos
// Os dados de featured e trending vêm da RAWG API
const emptyFeatured = {
  id: 0,
  title: '',
  coverImageUrl: '',
  backgroundImageUrl: '',
  offers: [],
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
      console.log('Resultados brutos recebidos:', JSON.stringify(results, null, 2));
      console.log('Itens recebidos:', results.items);
      
      // Verifica se há itens na resposta
      if (!results.items || results.items.length === 0) {
        setError('Conteúdo não encontrado');
        setGames([]);
      } else {
        // Log de cada item para debug
        results.items.forEach((item, index) => {
          console.log(`Item ${index}:`, {
            id: item.id,
            name: item.name,
            tiny_image: item.tiny_image,
            img: item.img,
            price: item.price
          });
        });
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
      console.log('RAWG Featured response:', JSON.stringify(data, null, 2));
      
      // Adapta os dados para o formato esperado pelo frontend
      if (data) {
        const imgUrl = data.img || data.tiny_image || data.background_image || '';
        console.log('Featured Image URL:', imgUrl);
        const normalizedUrl = normalizeImageUrl(imgUrl);
        console.log('Featured Normalized URL:', normalizedUrl);
        
        const adaptedFeatured = {
          id: data.idGame || data.id || 0,
          title: data.name || 'Sem título',
          coverImageUrl: normalizedUrl,
          backgroundImageUrl: normalizedUrl,
          genres: data.nomeGeneros || data.genres || '',
          rating: data.avaliacao || data.rating || 0,
          platforms: data.nomePlataformas || data.platforms || '',
          // Adicionar ofertas vazias para evitar erros
          offers: [],
        };
        console.log('Adapted Featured:', adaptedFeatured);
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
      console.log('RAWG Trending response:', JSON.stringify(data, null, 2));
      
      // Adapta os dados para o formato esperado pelo frontend
      if (data && Array.isArray(data)) {
        const adaptedTrending = data.map(game => {
          // Debug cada campo
          console.log('Game from RAWG:', JSON.stringify(game));
          const imgUrl = game.img || game.tiny_image || game.background_image || '';
          console.log('Image URL to normalize:', imgUrl);
          const normalizedUrl = normalizeImageUrl(imgUrl);
          console.log('Normalized URL:', normalizedUrl);
          
          return {
            id: game.idGame || game.id || 0,
            title: game.name || 'Sem título',
            coverImageUrl: normalizedUrl,
            backgroundImageUrl: normalizedUrl,
            genres: game.nomeGeneros || game.genres || '',
            rating: game.avaliacao || game.rating || 0,
            platforms: game.nomePlataformas || game.platforms || '',
            // Adicionar ofertas vazias para evitar erros
            offers: [],
          };
        });
        console.log('Adapted trending:', adaptedTrending);
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
    // Busca nos jogos recentes (trending)
    const game = trending.find(g => g.id === gameId) || 
                 (featured.id === gameId ? featured : null);
    setSelectedGame(game || null);
  }, [trending, featured]);

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
