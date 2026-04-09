import { Flame } from 'lucide-react';
import GameCard from '../Game/GameCard';

function SkeletonCard() {
  return (
    <div className="bg-zinc-900 rounded-xl overflow-hidden animate-pulse">
      <div className="h-40 bg-zinc-800" />
      <div className="p-3 space-y-2">
        <div className="h-3.5 bg-zinc-800 rounded-full w-3/4" />
        <div className="h-3 bg-zinc-800 rounded-full w-1/2" />
      </div>
    </div>
  );
}

function TrendingSection({ trending, onGameClick, isLoading }) {
  return (
    <div className="w-full">
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <Flame className="w-5 h-5 text-orange-400" />
          <h3 className="text-lg font-bold font-display">Jogos Populares</h3>
        </div>
        {!isLoading && trending.length > 0 && (
          <span className="text-xs text-zinc-500 bg-zinc-900 border border-zinc-800 px-2.5 py-1 rounded-full">
            {trending.length} jogos
          </span>
        )}
      </div>

      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-4">
        {isLoading
          ? Array.from({ length: 10 }).map((_, i) => <SkeletonCard key={i} />)
          : trending.map((game) => (
              <GameCard
                key={game.id}
                game={game}
                onClick={onGameClick}
                variant="trending"
              />
            ))}
      </div>
    </div>
  );
}

export default TrendingSection;
