import { useState } from 'react';
import { Search, Gamepad2, ExternalLink, TrendingDown } from 'lucide-react';
import { mockGameResult } from './mockData';

function App() {
  const [searchTerm, setSearchTerm] = useState('');
  const [gameData, setGameData] = useState(null);

  const handleSearch = (e) => {
    e.preventDefault();
    if (!searchTerm.trim()) return;
    
    // Simulamos a resposta da API guardando o mock no estado
    setGameData(mockGameResult);
  };

  // Nova função para repor o estado inicial
  const handleReset = () => {
    setGameData(null);
    setSearchTerm('');
  };

  return (
    <div className="min-h-screen bg-zinc-950 text-zinc-50 flex flex-col items-center p-4 py-12 font-sans">
      
      <div className="w-full max-w-5xl flex flex-col items-center animate-fade-in">
        
        {/* Header / Hero Section - Agora com a função de Reset no clique */}
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
            placeholder="Ex: Valorant, Sea of Thieves, Rainbow Six..."
            className="w-full bg-zinc-900 border border-zinc-800 rounded-full py-5 pl-8 pr-16 text-lg text-white placeholder-zinc-500 focus:outline-none focus:border-purple-500 focus:ring-1 focus:ring-purple-500 transition-all shadow-xl"
          />
          <button
            type="submit"
            className="absolute right-2 top-2 bottom-2 bg-purple-600 hover:bg-purple-500 text-white rounded-full p-4 transition-colors flex items-center justify-center"
          >
            <Search className="w-6 h-6" />
          </button>
        </form>

        {/* Área de Resultados (Apenas visível se gameData não for nulo) */}
        {gameData && (
          <div className="w-full grid grid-cols-1 md:grid-cols-3 gap-8 items-start">
            
            {/* Coluna da Esquerda: Dados do Jogo */}
            <div className="col-span-1 bg-zinc-900 border border-zinc-800 rounded-2xl overflow-hidden shadow-2xl">
              <img 
                src={gameData.coverImageUrl} 
                alt={gameData.title} 
                className="w-full h-48 object-cover"
              />
              <div className="p-6">
                <h2 className="text-2xl font-bold mb-2">{gameData.title}</h2>
                <p className="text-purple-400 text-sm font-semibold mb-4 border border-purple-900 bg-purple-950/30 inline-block px-3 py-1 rounded-full">
                  {gameData.developer}
                </p>
                <p className="text-zinc-400 text-sm leading-relaxed">
                  {gameData.description}
                </p>
              </div>
            </div>

            {/* Coluna da Direita: Lista de Preços */}
            <div className="col-span-1 md:col-span-2 flex flex-col gap-4">
              <h3 className="text-xl font-semibold mb-2 flex items-center gap-2">
                <TrendingDown className="text-emerald-400" />
                Melhores Ofertas
              </h3>

              {/* Mapeamento das ofertas para gerar os cartões */}
              {gameData.offers.map((offer) => (
                <div 
                  key={offer.id} 
                  className="bg-zinc-900 border border-zinc-800 rounded-xl p-5 flex items-center justify-between hover:border-purple-500/50 transition-colors"
                >
                  <div className="flex items-center gap-4">
                    <img 
                      src={offer.store.logoUrl} 
                      alt={offer.store.name} 
                      className="w-12 h-12 object-contain bg-zinc-800 rounded-lg p-2"
                    />
                    <div>
                      <h4 className="font-bold text-lg">{offer.store.name}</h4>
                      <p className="text-zinc-500 text-sm line-through">
                        Menor histórico: R$ {offer.historicalLowPrice.toFixed(2)}
                      </p>
                    </div>
                  </div>

                  <div className="flex items-center gap-6">
                    <div className="text-right">
                      <p className="text-emerald-400 font-extrabold text-2xl">
                        R$ {offer.currentPrice.toFixed(2)}
                      </p>
                    </div>
                    
                    <a 
                      href={offer.dealUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="bg-zinc-800 hover:bg-zinc-700 text-white p-3 rounded-xl transition-colors"
                    >
                      <ExternalLink className="w-5 h-5" />
                    </a>
                  </div>
                </div>
              ))}
            </div>

          </div>
        )}

      </div>
    </div>
  );
}

export default App;