import { useState, useCallback } from 'react';
import { gameService } from '../services/api';

// Cache de capsule por título — persiste enquanto o módulo estiver carregado (SPA)
const capsuleCache = new Map();

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

  const adaptGame = (data) => {
    const imgUrl = data.img || data.tiny_image || data.background_image || '';
    const backgroundUrl = normalizeImageUrl(imgUrl);
    const capsuleUrl = data.steamCapsuleUrl || null;
    const rawgId = data.idGame || data.id || 0;
    return {
      id: rawgId,
      rawgId,
      title: data.name || 'Sem título',
      // coverImageUrl: capsule com logo (cards de trending) — fallback para wide art
      coverImageUrl: capsuleUrl || backgroundUrl,
      // backgroundImageUrl: wide art sem logo (destaque principal hero)
      backgroundImageUrl: backgroundUrl,
      tinyImageUrl: backgroundUrl,
      genres: data.rawgDetails?.nomeGeneros || data.nomeGeneros || data.genres || '',
      rating: data.rawgDetails?.avaliacao || data.avaliacao || data.rating || 0,
      platforms: data.rawgDetails?.nomePlataformas || data.nomePlataformas || data.platforms || '',
      offers: [],
      screenshots: data.screenshots || [],
    };
  };

  // Normaliza nome: lowercase, sem pontuação
  const normName = (s) => (s || '').toLowerCase().replace(/[^a-z0-9\s]/g, '').trim();

  // Busca Steam App ID pelo nome — retorna capsule URL + steamAppId + steamName
  const resolveSteamCapsule = async (game) => {
    const cacheKey = normName(game.title);
    if (capsuleCache.has(cacheKey)) return capsuleCache.get(cacheKey);

    try {
      const res = await gameService.searchGames(game.title);
      const items = res?.items ?? [];
      const target = cacheKey;
      const match =
        items.find(i => normName(i.name) === target) ||
        items.find(i => {
          const n = normName(i.name);
          return Math.abs(n.length - target.length) <= 4 && (n.startsWith(target) || target.startsWith(n));
        });
      const result = match?.id
        ? {
            rawgId: game.rawgId,
            capsule: `https://cdn.akamai.steamstatic.com/steam/apps/${match.id}/header.jpg`,
            steamAppId: match.id,
            steamName: match.name,
          }
        : null;
      capsuleCache.set(cacheKey, result);
      return result;
    } catch {
      capsuleCache.set(cacheKey, null); // cacheia falha também, evita retry em loop
      return null;
    }
  };

  // Carrega featured + trending em uma única chamada — sem duplicatas garantidas pelo backend
  const loadHomeData = useCallback(async () => {
    setIsLoading(true);

    try {
      const data = await gameService.getHomeData(10);

      const featuredGame = data?.featured ? adaptGame(data.featured) : null;
      const trendingGames = data?.trending?.map(adaptGame) ?? [];

      if (featuredGame) setFeatured(featuredGame);
      if (trendingGames.length) setTrending(trendingGames);

      // Enriquece trending + featured com Steam capsule em background (sem bloquear o render)
      const toEnrich = [...trendingGames, ...(featuredGame ? [featuredGame] : [])].filter(g => g.rawgId);
      if (toEnrich.length === 0) return;

      Promise.allSettled(toEnrich.map(resolveSteamCapsule)).then(results => {
        const capsuleMap = {};
        results.forEach(r => {
          if (r.status === 'fulfilled' && r.value) capsuleMap[r.value.rawgId] = r.value;
        });
        if (Object.keys(capsuleMap).length === 0) return;

        const applyEnrich = (g) => {
          const e = capsuleMap[g.rawgId];
          if (!e) return g;
          return { ...g, coverImageUrl: e.capsule, steamAppId: e.steamAppId, steamName: e.steamName };
        };
        setTrending(prev => prev.map(applyEnrich));
        setFeatured(prev => prev ? applyEnrich(prev) : prev);
      });
    } catch (err) {
      console.error('Erro ao carregar dados da home:', err);
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
    loadHomeData,
    selectGame,
    clearSelection,
    clearSearch,
    clearError,
  };
};

export default useGames;
