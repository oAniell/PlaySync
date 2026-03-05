import { useState } from 'react';
import { Search, Gamepad2, ExternalLink, TrendingDown, Star, Sparkles, Joystick } from 'lucide-react';
import { mockGames, featuredGame, trendingGames, searchGames } from './mockData';

// URL fallback para imagens quebradas
const FALLBACK_IMAGE = 'https://images.unsplash.com/photo-1612287230217-8c7684717995?w=400&h=300&fit=crop';

function App() {
  const [searchTerm, setSearchTerm] = useState('');
  const [searchResults, setSearchResults] = useState(null);
  const [selectedGame, setSelectedGame] = useState(null);
  const [isSearching, setIsSearching] = useState(false);

  const handleSearch = (e) => {
    e.preventDefault();
    if (!searchTerm.trim()) return;
    
    setIsSearching(true);
    
    // Simula uma busca com delay para dar feedback visual
    setTimeout(() => {
      const results = searchGames(searchTerm);
      setSearchResults(results);
      setSelectedGame(null);
      setIsSearching(false);
    }, 300);
  };

  const handleReset = () => {
    setSearchResults(null);
    setSelectedGame(null);
    setSearchTerm('');
  };

  const handleGameClick = (game) => {
    setSelectedGame(game);
    setSearchResults(null);
  };

  // Encontrar menor preço entre todas as ofertas
  const getLowestPrice = (offers) => {
    if (!offers || offers.length === 0) return null;
    return offers.reduce((min, offer) => 
      offer.currentPrice < min.currentPrice ? offer : min
    , offers[0]);
  };

  return (
    <div className="min-h-screen bg-zinc-950 text-zinc-50 flex flex-col items-center p-4 py-12 font-sans">
      
      <div className="w-full max-w-6xl flex flex-col items-center animate-fade-in">
        
        {/* Header / Hero Section */}
        <div className="text-center mb-10">
          <div 
            onClick={handleReset}
            className="flex items-center justify-center gap-3 mb-4 cursor-pointer hover:opacity-80 transition-opacity"
            title="Voltar para o início"
          >
            <Gamepad2 className="w-14 h-14 text-purple-500" />
            <h1 className="text-6xl font-extrabold tracking-tight">PlaySync</h1>
          </div>
          <p className="text-zinc-400 text-xl">
            O agregador definitivo. Encontre o melhor preço para o seu próximo jogo.
          </p>
        </div>

        {/* Barra de Pesquisa */}
        <form onSubmit={handleSearch} className="w-full max-w-3xl relative group mb-12">
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Ex: Valorant, Sea of Thieves, Elden Ring..."
            className="w-full bg-zinc-900 border border-zinc-800 rounded-full py-5 pl-8 pr-16 text-lg text-white placeholder-zinc-500 focus:outline-none focus:border-purple-500 focus:ring-1 focus:ring-purple-500 transition-all shadow-xl"
          />
          <button
            type="submit"
            disabled={isSearching}
            className="absolute right-2 top-2 bottom-2 bg-purple-600 hover:bg-purple-500 disabled:bg-purple-800 text-white rounded-full p-4 transition-colors flex items-center justify-center"
          >
            {isSearching ? (
              <div className="w-6 h-6 border-2 border-white border-t-transparent rounded-full animate-spin" />
            ) : (
              <Search className="w-6 h-6" />
            )}
          </button>
        </form>

        {/* Área de Resultados da Busca */}
        {searchResults && searchResults.length > 0 && (
          <div className="w-full mb-12">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-xl font-semibold flex items-center gap-2">
                <Search className="text-purple-400 w-5 h-5" />
                Resultados para "{searchTerm}"
              </h3>
              <span className="text-zinc-500 text-sm">{searchResults.length} jogos encontrados</span>
            </div>
            
            <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
              {searchResults.map((game) => {
                const lowestPrice = getLowestPrice(game.offers);
                return (
                  <div 
                    key={game.id}
                    onClick={() => handleGameClick(game)}
                    className="bg-zinc-900 border border-zinc-800 rounded-xl overflow-hidden cursor-pointer hover:border-purple-500/50 hover:scale-105 transition-all group"
                  >
                    <div className="relative">
                      <img 
                        src={game.coverImageUrl} 
                        alt={game.title} 
                        className="w-full h-32 object-cover"
                        onError={(e) => { e.target.src = FALLBACK_IMAGE; }}
                      />
                      <div className="absolute inset-0 bg-gradient-to-t from-zinc-950 to-transparent opacity-60" />
                    </div>
                    <div className="p-3">
                      <h4 className="font-bold text-sm truncate group-hover:text-purple-400 transition-colors">
                        {game.title}
                      </h4>
                      <div className="flex items-center justify-between mt-2">
                        <span className="text-zinc-400 text-xs">{game.developer}</span>
                        {lowestPrice && (
                          <span className="text-emerald-400 font-bold text-sm">
                            {lowestPrice.currentPrice === 0 ? 'Grátis' : `R$ ${lowestPrice.currentPrice.toFixed(2)}`}
                          </span>
                        )}
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        )}

        {/* Área de Resultados da Busca - Sem resultados */}
        {searchResults && searchResults.length === 0 && (
          <div className="w-full text-center py-12">
            <Joystick className="w-16 h-16 text-zinc-600 mx-auto mb-4" />
            <h3 className="text-xl font-semibold text-zinc-400 mb-2">Nenhum jogo encontrado</h3>
            <p className="text-zinc-500">Tente buscar por outro título ou termo</p>
          </div>
        )}

        {/* Detalhes do Jogo Selecionado */}
        {selectedGame && (
          <div className="w-full grid grid-cols-1 lg:grid-cols-4 gap-8 items-start mb-12">
            
            {/* Coluna da Esquerda: Dados do Jogo */}
            <div className="lg:col-span-1 bg-zinc-900 border border-zinc-800 rounded-2xl overflow-hidden shadow-2xl">
              <div className="relative">
                <img 
                  src={selectedGame.coverImageUrl} 
                  alt={selectedGame.title} 
                  className="w-full h-56 object-cover"
                  onError={(e) => { e.target.src = FALLBACK_IMAGE; }}
                />
                <div className="absolute top-3 right-3 bg-purple-600 text-white text-xs font-bold px-2 py-1 rounded-full flex items-center gap-1">
                  <Star className="w-3 h-3 fill-current" />
                  {selectedGame.rating}
                </div>
              </div>
              <div className="p-6">
                <h2 className="text-xl font-bold mb-2 line-clamp-2">{selectedGame.title}</h2>
                <div className="flex flex-wrap gap-2 mb-3">
                  {selectedGame.genres.map((genre, index) => (
                    <span key={index} className="text-purple-400 text-xs font-semibold border border-purple-900 bg-purple-950/30 px-2 py-1 rounded-full">
                      {genre}
                    </span>
                  ))}
                </div>
                <p className="text-zinc-400 text-sm mb-3">
                  <span className="text-zinc-500">Desenvolvedora:</span> {selectedGame.developer}
                </p>
                <p className="text-zinc-400 text-sm mb-3">
                  <span className="text-zinc-500">Editora:</span> {selectedGame.publisher}
                </p>
                <p className="text-zinc-400 text-sm leading-relaxed line-clamp-4">
                  {selectedGame.description}
                </p>
                <button 
                  onClick={handleReset}
                  className="mt-4 w-full bg-zinc-800 hover:bg-zinc-700 text-white py-2 rounded-lg transition-colors text-sm"
                >
                  ← Voltar aos jogos
                </button>
              </div>
            </div>

            {/* Coluna da Direita: Lista de Preços */}
            <div className="lg:col-span-3 flex flex-col gap-4">
              <h3 className="text-xl font-semibold flex items-center gap-2">
                <TrendingDown className="text-emerald-400" />
                Melhores Ofertas
              </h3>

              {/* Mapeamento das ofertas para gerar os cartões */}
              {selectedGame.offers.map((offer) => (
                <div 
                  key={offer.id} 
                  className="bg-zinc-900 border border-zinc-800 rounded-xl p-5 flex items-center justify-between hover:border-purple-500/50 transition-colors"
                >
                  <div className="flex items-center gap-4">
                    <img 
                      src={offer.store.logoUrl} 
                      alt={offer.store.name} 
                      className="w-14 h-14 object-contain bg-zinc-800 rounded-lg p-2"
                    />
                    <div>
                      <h4 className="font-bold text-lg">{offer.store.name}</h4>
                      <p className="text-zinc-500 text-sm">
                        Menor histórico: <span className="text-emerald-400">R$ {offer.historicalLowPrice.toFixed(2)}</span>
                      </p>
                    </div>
                  </div>

                  <div className="flex items-center gap-6">
                    <div className="text-right">
                      <p className="text-emerald-400 font-extrabold text-3xl">
                        {offer.currentPrice === 0 ? 'Grátis' : `R$ ${offer.currentPrice.toFixed(2)}`}
                      </p>
                      {offer.currentPrice > offer.historicalLowPrice && (
                        <p className="text-zinc-500 text-xs">
                          Acima do mínimo histórico
                        </p>
                      )}
                      {offer.currentPrice <= offer.historicalLowPrice && (
                        <p className="text-emerald-400 text-xs font-semibold">
                          ✨ No menor preço!
                        </p>
                      )}
                    </div>
                    
                    <a 
                      href={offer.dealUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="bg-purple-600 hover:bg-purple-500 text-white p-3 rounded-xl transition-colors flex items-center gap-2"
                    >
                      <span className="text-sm font-medium">Comprar</span>
                      <ExternalLink className="w-5 h-5" />
                    </a>
                  </div>
                </div>
              ))}
            </div>

          </div>
        )}

        {/* Seção de Jogos em Destaque - Apenas quando nenhuma busca ou resultado */}
        {!selectedGame && !searchResults && (
          <>
            {/* Jogo em Destaque */}
            <div className="w-full mb-12">
              <h3 className="text-xl font-semibold mb-4 flex items-center gap-2">
                <Sparkles className="text-yellow-400" />
                Destaque da Semana
              </h3>
              
              <div 
                className="relative w-full h-72 md:h-96 rounded-2xl overflow-hidden cursor-pointer group"
                onClick={() => handleGameClick(featuredGame)}
              >
                <img 
                  src={featuredGame.backgroundImageUrl || featuredGame.coverImageUrl} 
                  alt={featuredGame.title}
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                  onError={(e) => { e.target.src = FALLBACK_IMAGE; }}
                />
                <div className="absolute inset-0 bg-gradient-to-t from-zinc-950 via-zinc-950/60 to-transparent" />
                
                <div className="absolute bottom-0 left-0 right-0 p-6 md:p-8">
                  <div className="flex items-center gap-3 mb-2">
                    <span className="bg-yellow-500 text-zinc-950 text-xs font-bold px-2 py-1 rounded">
                      DESTAQUE
                    </span>
                    <span className="text-zinc-400 text-sm">{featuredGame.developer}</span>
                  </div>
                  <h2 className="text-3xl md:text-4xl font-bold mb-2">{featuredGame.title}</h2>
                  <p className="text-zinc-300 text-sm md:text-base max-w-2xl mb-4 line-clamp-2">
                    {featuredGame.description}
                  </p>
                  <div className="flex items-center gap-4">
                    <span className="text-emerald-400 font-bold text-2xl">
                      R$ {getLowestPrice(featuredGame.offers)?.currentPrice.toFixed(2)}
                    </span>
                    <span className="text-zinc-500 text-sm">
                      a partir de {featuredGame.offers.length} lojas
                    </span>
                  </div>
                </div>
              </div>
            </div>

            {/* Jogos Populares */}
            <div className="w-full">
              <h3 className="text-xl font-semibold mb-4 flex items-center gap-2">
                <TrendingDown className="text-purple-400" />
                Jogos Populares
              </h3>
              
              <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-4">
                {trendingGames.map((game) => {
                  const lowestPrice = getLowestPrice(game.offers);
                  return (
                    <div 
                      key={game.id}
                      onClick={() => handleGameClick(game)}
                      className="bg-zinc-900 border border-zinc-800 rounded-xl overflow-hidden cursor-pointer hover:border-purple-500/50 hover:scale-105 hover:shadow-xl transition-all group"
                    >
                      <div className="relative">
                        <img 
                          src={game.coverImageUrl} 
                          alt={game.title} 
                          className="w-full h-40 object-cover"
                          onError={(e) => { e.target.src = FALLBACK_IMAGE; }}
                        />
                        <div className="absolute top-2 right-2 bg-purple-600 text-white text-xs font-bold px-2 py-0.5 rounded-full flex items-center gap-1">
                          <Star className="w-3 h-3 fill-current" />
                          {game.rating}
                        </div>
                        <div className="absolute inset-0 bg-gradient-to-t from-zinc-950 to-transparent opacity-40" />
                      </div>
                      <div className="p-3">
                        <h4 className="font-bold text-sm truncate mb-1 group-hover:text-purple-400 transition-colors">
                          {game.title}
                        </h4>
                        <div className="flex flex-wrap gap-1 mb-2">
                          {game.genres.slice(0, 2).map((genre, idx) => (
                            <span key={idx} className="text-zinc-500 text-xs">
                              {genre}
                            </span>
                          ))}
                        </div>
                        <div className="flex items-center justify-between mt-2">
                          <span className="text-zinc-400 text-xs">{game.developer}</span>
                          {lowestPrice && (
                            <span className="text-emerald-400 font-bold">
                              {lowestPrice.currentPrice === 0 ? 'Grátis' : `R$ ${lowestPrice.currentPrice.toFixed(2)}`}
                            </span>
                          )}
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>

            {/* Ver todos os jogos */}
            <div className="w-full mt-8 text-center">
              <p className="text-zinc-500">
                ou busque por qualquer outro jogo acima ↑↑
              </p>
            </div>
          </>
        )}

      </div>
      
      {/* Footer simples */}
      <footer className="mt-16 text-center text-zinc-600 text-sm">
        <p>© 2026 PlaySync - Comparador de preços de jogos</p>
        <p className="mt-1">
          Dados fornecidos por{" "}
          <a
            href="https://www.cheapshark.com"
            target="_blank"
            rel="noopener noreferrer"
            className="text-purple-500 hover:text-purple-400 transition-colors"
          >
            CheapShark
          </a>{" "}e{" "}
          <a
            href="https://rawg.io"
            target="_blank"
            rel="noopener noreferrer"
            className="text-purple-500 hover:text-purple-400 transition-colors"
          >
            RAWG
          </a>
        </p>
      </footer>
    </div>
  );
}

export default App;
