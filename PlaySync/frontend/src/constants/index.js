// Constantes da aplicação

// Configurações de ambiente
export const APP_CONFIG = {
  name: 'PlaySync',
  version: '1.0.0',
  description: 'Comparador de preços de jogos digitais',
  apiTimeout: 10000, // 10 segundos
};

// IDs das lojas (para referência quando precisarmos mapear)
export const STORE_IDS = {
  STEAM: 1,
  GOG: 2,
  EPIC_GAMES: 3,
  UBISOFT_STORE: 4,
  XBOX: 5,
  HUMBLE_STORE: 7,
  ITCH_IO: 11,
  GAMEBILLET: 15,
};

// Cores do tema
export const THEME_COLORS = {
  primary: '#8b5cf6', // purple-500
  primaryHover: '#7c3aed', // purple-600
  secondary: '#6366f1', // indigo-500
  success: '#10b981', // emerald-400
  danger: '#ef4444', // red-500
  warning: '#f59e0b', // amber-500
  background: '#09090b', // zinc-950
  surface: '#18181b', // zinc-900
  border: '#27272a', // zinc-800
  text: '#fafafa', // zinc-50
  textSecondary: '#a1a1aa', // zinc-400
};

// Mensagens de erro
export const ERROR_MESSAGES = {
  networkError: 'Erro de conexão. Verifique sua internet.',
  serverError: 'Erro no servidor. Tente novamente mais tarde.',
  notFound: 'Jogo não encontrado.',
  generic: 'Ocorreu um erro. Tente novamente.',
};

// Labels da interface
export const LABELS = {
  searchPlaceholder: 'Buscar jogo...',
  searchButton: 'Buscar',
  featuredSection: 'Destaque da Semana',
  trendingSection: 'Jogos Populares',
  offersSection: 'Melhores Ofertas',
  buyButton: 'Comprar',
  free: 'Grátis',
  historicalLow: 'Menor histórico',
  currentPrice: 'Preço atual',
  developer: 'Desenvolvedora',
  publisher: 'Editora',
  genres: 'Gêneros',
  rating: 'Avaliação',
  loading: 'Carregando...',
  noResults: 'Nenhum resultado encontrado',
  backToHome: 'Voltar ao início',
};
