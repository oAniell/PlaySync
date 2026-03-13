// Configuração das URLs da API
// Backend Steam: POST /api-playsync/search?termo=xxx

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

// Endpoints da API
export const API_ENDPOINTS = {
  // Buscar jogos por termo (endpoint do backend Steam)
  SEARCH_GAMES: (term) => `${API_BASE_URL}/api-playsync/search?termo=${encodeURIComponent(term)}`,
  
  // Obter detalhes de um jogo específico
  GAME_DETAILS: (gameId) => `${API_BASE_URL}/api/games/${gameId}`,
  
  // Obter jogos em destaque
  FEATURED_GAMES: `${API_BASE_URL}/api/games/featured`,
  
  // Obter lista de lojas
  STORES: `${API_BASE_URL}/api/stores`,
};

// Função genérica para fazer requisições
const fetchAPI = async (url, options = {}) => {
  const defaultOptions = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const mergedOptions = { ...defaultOptions, ...options };

  try {
    const response = await fetch(url, mergedOptions);
    
    if (!response.ok) {
      // Tenta obter a mensagem de erro do corpo da resposta
      let errorMessage = `HTTP error! status: ${response.status}`;
      try {
        const errorData = await response.json();
        // O backend retorna a mensagem no campo "mensagem"
        errorMessage = errorData.mensagem || errorData.message || errorData.reason || errorMessage;
      } catch (e) {
        // Se não conseguir ler o corpo, usa a mensagem padrão
      }
      throw new Error(errorMessage);
    }
    
    return await response.json();
  } catch (error) {
    console.error('API Error:', error);
    throw error;
  }
};

// Serviços da API
export const gameService = {
  // Buscar jogos por termo
  searchGames: async (term) => {
    return fetchAPI(API_ENDPOINTS.SEARCH_GAMES(term), { method: 'POST' });
  },

  // Obter detalhes de um jogo
  getGameDetails: async (gameId) => {
    return fetchAPI(API_ENDPOINTS.GAME_DETAILS(gameId));
  },

  // Obter jogos em destaque
  getFeaturedGames: async () => {
    return fetchAPI(API_ENDPOINTS.FEATURED_GAMES);
  },

  // Obter lista de lojas
  getStores: async () => {
    return fetchAPI(API_ENDPOINTS.STORES);
  },
};

export default gameService;
