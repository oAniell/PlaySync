import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import FeaturedGame from '../components/Hero/FeaturedGame';
import TrendingSection from '../components/Hero/TrendingSection';
import SearchBar from '../components/Search/SearchBar';

function HomePage({ featured, trending, isLoading, onGameClick }) {
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState('');

  const handleSearch = (e) => {
    e.preventDefault();
    if (!searchTerm.trim()) return;
    navigate(`/search?q=${encodeURIComponent(searchTerm.trim())}`);
  };

  const handleGameClick = (game) => {
    onGameClick(game);
    navigate(`/game/${game.id}`, { state: { fromClick: true } });
  };

  return (
    <>
      <div className="text-center mb-10 animate-slide-up">
        <h1 className="text-5xl md:text-7xl font-black font-display mb-4 tracking-tight leading-none">
          <span
            style={{
              background: 'linear-gradient(135deg, #60a5fa 0%, #3b82f6 55%, #06b6d4 100%)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              backgroundClip: 'text',
            }}
          >
            Play
          </span>
          <span className="text-zinc-50">Sync</span>
        </h1>
        <p className="text-zinc-400 text-lg md:text-xl max-w-lg mx-auto leading-relaxed">
          Compare preços de jogos em múltiplas lojas.{' '}
          <span className="text-zinc-300">Sempre no melhor preço.</span>
        </p>
      </div>

      <div className="flex justify-center mb-12">
        <SearchBar
          value={searchTerm}
          onChange={setSearchTerm}
          onSubmit={handleSearch}
          isLoading={false}
        />
      </div>

      <div className="flex flex-col gap-14">
        <FeaturedGame
          featured={featured}
          onClick={handleGameClick}
          isLoading={isLoading && !featured?.title}
        />
        <TrendingSection
          trending={trending}
          onGameClick={handleGameClick}
          isLoading={isLoading && trending.length === 0}
        />
        {!isLoading && trending.length > 0 && (
          <p className="text-center text-zinc-600 text-sm -mt-6">
            ou busque por qualquer outro jogo acima ↑
          </p>
        )}
      </div>
    </>
  );
}

export default HomePage;
