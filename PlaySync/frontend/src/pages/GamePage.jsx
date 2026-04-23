import { useState, useEffect } from 'react';
import { useNavigate, useParams, useSearchParams, useLocation } from 'react-router-dom';
import GameDetail from '../components/Game/GameDetail';
import SearchBar from '../components/Search/SearchBar';

function GamePage({ selectedGame, isLoading, onLoadGame }) {
  const navigate = useNavigate();
  const { id } = useParams();
  const location = useLocation();
  const [, setSearchParams] = useSearchParams();
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    if (id && !location.state?.fromClick) onLoadGame(id);
  }, [id]);

  const handleSearch = (e) => {
    e.preventDefault();
    if (!searchTerm.trim()) return;
    navigate(`/search?q=${encodeURIComponent(searchTerm.trim())}`);
  };

  return (
    <>
      <div className="text-center mb-6 animate-fade-in">
        <h1 className="text-3xl md:text-5xl font-black font-display tracking-tight leading-none">
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
      </div>

      <div className="flex justify-center mb-8 mt-2">
        <SearchBar
          value={searchTerm}
          onChange={setSearchTerm}
          onSubmit={handleSearch}
          isLoading={false}
        />
      </div>

      {selectedGame ? (
        <GameDetail game={selectedGame} onBack={() => navigate(-1)} />
      ) : !isLoading ? (
        <div className="text-center py-20 text-zinc-500 animate-fade-in">
          <p className="text-lg">Jogo não encontrado.</p>
          <button
            onClick={() => navigate(-1)}
            className="mt-4 text-blue-400 hover:text-blue-300 transition-colors text-sm"
          >
            ← Voltar
          </button>
        </div>
      ) : null}
    </>
  );
}

export default GamePage;
