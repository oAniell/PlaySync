import { Search, Joystick } from 'lucide-react';
import GameCard from '../Game/GameCard';

function SearchResults({ results, searchTerm, onGameClick }) {
  if (!results) return null;

  if (results.length === 0) {
    return (
      <div className="w-full text-center py-16 animate-fade-in">
        <div className="w-16 h-16 bg-zinc-900 border border-zinc-800 rounded-2xl flex items-center justify-center mx-auto mb-4">
          <Joystick className="w-8 h-8 text-zinc-600" />
        </div>
        <h3 className="text-lg font-bold font-display text-zinc-300 mb-2">Nenhum jogo encontrado</h3>
        <p className="text-zinc-500 text-sm">Tente buscar por outro título ou termo diferente</p>
      </div>
    );
  }

  return (
    <div className="w-full animate-fade-in">
      <div className="flex items-center justify-between mb-5">
        <div className="flex items-center gap-2">
          <Search className="w-4 h-4 text-blue-400" />
          <h3 className="text-base font-semibold font-display">
            Resultados para{' '}
            <span className="text-blue-400">"{searchTerm}"</span>
          </h3>
        </div>
        <span className="text-xs font-medium bg-zinc-900 border border-zinc-800 text-zinc-400 px-2.5 py-1 rounded-full">
          {results.length} {results.length === 1 ? 'jogo' : 'jogos'}
        </span>
      </div>

      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        {results.map((game) => (
          <GameCard
            key={game.id}
            game={game}
            onClick={onGameClick}
            variant="search"
          />
        ))}
      </div>
    </div>
  );
}

export default SearchResults;
