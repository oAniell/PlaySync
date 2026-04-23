import { useState, useEffect } from 'react';
import { useGames } from './hooks/useGames';
import { gameService } from './services/api';
import { enrichWithMockData } from './utils/mockGameData';

import Navbar from './components/Common/Navbar';
import Footer from './components/Common/Footer';
import SearchBar from './components/Search/SearchBar';
import SearchResults from './components/Search/SearchResults';
import FeaturedGame from './components/Hero/FeaturedGame';
import TrendingSection from './components/Hero/TrendingSection';
import GameDetail from './components/Game/GameDetail';

// ─── Helpers ────────────────────────────────────────────────
const FALLBACK_IMAGE = 'https://images.unsplash.com/photo-1612287230217-8c7684717995?w=400&h=300&fit=crop';

const normalizeImageUrl = (url) => {
  if (!url) return FALLBACK_IMAGE;
  if (url.startsWith('//')) return 'https:' + url;
  return url;
};

const steamCapsuleUrl = (appId) =>
  `https://cdn.akamai.steamstatic.com/steam/apps/${appId}/header.jpg`;

const adaptSteamData = (steamData) => {
  const items = Array.isArray(steamData) ? steamData : steamData?.items ?? [];
  return items.map((item, index) => {
    const appId = item.id || item.idGame;
    // Chave única: appId real ou índice como fallback para evitar chaves duplicadas
    const uniqueId = appId || `fallback-${index}`;
    const capsule = appId ? steamCapsuleUrl(appId) : null;
    const tinyImage = normalizeImageUrl(item.tiny_image || item.img);
    return {
      id: uniqueId,
      title: item.name,
      coverImageUrl: capsule || tinyImage,
      backgroundImageUrl: capsule || tinyImage,
      tinyImageUrl: tinyImage, // fallback quando a capsule não carrega
      developer: 'Steam',
      offers: item.price
        ? [{ id: uniqueId, currentPrice: item.price.final || 0, originalPrice: item.price.initial || 0 }]
        : [{ id: uniqueId, currentPrice: 0, originalPrice: 0 }],
    };
  });
};

// ─── App ─────────────────────────────────────────────────────
function App() {
  const [searchTerm, setSearchTerm] = useState('');
  const [hasSearched, setHasSearched] = useState(false);
  const [selectedGame, setSelectedGame] = useState(null);

  const {
    games,
    featured,
    trending,
    isLoading,
    error,
    search,
    clearSearch,
    loadHomeData,
  } = useGames();

  useEffect(() => {
    loadHomeData();
  }, [loadHomeData]);

  // ── Handlers ──────────────────────────────────────────────
  const handleSearch = async (e) => {
    e.preventDefault();
    if (!searchTerm.trim()) return;
    setHasSearched(true);
    setSelectedGame(null);
    await search(searchTerm);
  };

  const handleReset = () => {
    setSelectedGame(null);
    setSearchTerm('');
    setHasSearched(false);
    clearSearch();
  };

  const handleGameClick = async (game) => {
    const needsPrices =
      !game.offers || game.offers.length === 0 || game.offers[0]?.currentPrice === 0;

    // Steam App ID: direto no objeto (RAWG games enriquecidos) ou derivado do id (busca Steam)
    const resolvedSteamAppId = game.steamAppId || (typeof game.id === 'number' ? game.id : null);
    // Nome correto na Steam: evita buscar pelo nome RAWG que pode não existir na Steam
    const searchTitle = game.steamName || game.title;

    let enrichedGame = game;

    if (needsPrices) {
      try {
        const results = await gameService.searchGames(searchTitle);
        if (results?.items?.length > 0) {
          const steamGame = adaptSteamData({ items: results.items })[0];
          enrichedGame = {
            ...enrichedGame,
            id: steamGame.id,
            rawgId: game.rawgId,
            coverImageUrl: game.coverImageUrl || steamGame.coverImageUrl,
            backgroundImageUrl: game.backgroundImageUrl || steamGame.backgroundImageUrl,
            offers: steamGame.offers,
          };
        }
      } catch { /* sem preços, ok */ }
    }

    const activeSteamAppId = resolvedSteamAppId || (typeof enrichedGame.id === 'number' ? enrichedGame.id : null);
    const willFetchScreenshots = !!activeSteamAppId || !!game.rawgId;

    const base = enrichWithMockData(enrichedGame);
    setSelectedGame({
      ...base,
      screenshots: base.screenshots?.length > 0 ? base.screenshots : [],
      screenshotsLoading: willFetchScreenshots,
    });
    window.scrollTo({ top: 0, behavior: 'instant' });

    const applyScreenshots = (screenshots) => {
      setSelectedGame((current) =>
        current ? { ...current, screenshots: screenshots?.length > 0 ? screenshots : current.screenshots, screenshotsLoading: false } : current
      );
    };

    // Prefere Steam appdetails (ID exato) para qualquer jogo que tenha steamAppId
    if (activeSteamAppId) {
      gameService.getSteamScreenshots(activeSteamAppId).then(applyScreenshots).catch(() => applyScreenshots([]));
    } else if (game.rawgId) {
      gameService.getGameScreenshots(game.rawgId).then(applyScreenshots).catch(() => applyScreenshots([]));
    }
  };

  // ── Derived state ─────────────────────────────────────────
  const displayResults = hasSearched
    ? games.length > 0
      ? (() => {
          const seen = new Set();
          return adaptSteamData({ items: games }).filter(g => {
            const k = String(g.id);
            if (seen.has(k)) return false;
            seen.add(k);
            return true;
          });
        })()
      : []
    : null;

  const showHome = !selectedGame && !hasSearched;

  // ── Render ────────────────────────────────────────────────
  return (
    <div className="min-h-screen bg-zinc-950 text-zinc-50 flex flex-col">
      <Navbar onReset={handleReset} />

      <main className="flex-1 w-full max-w-6xl mx-auto px-4 sm:px-6 py-8 sm:py-12">

        {/* Hero heading */}
        {showHome ? (
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
        ) : (
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
        )}

        {/* Search bar */}
        <div className={`flex justify-center ${showHome ? 'mb-12' : 'mb-8 mt-2'}`}>
          <SearchBar
            value={searchTerm}
            onChange={setSearchTerm}
            onSubmit={handleSearch}
            isLoading={isLoading}
          />
        </div>

        {/* Error message */}
        {error && (
          <div className="max-w-2xl mx-auto mb-6 p-4 bg-red-950/30 border border-red-500/30 rounded-xl text-red-400 text-sm text-center animate-fade-in">
            {error}
          </div>
        )}

        {/* Game detail */}
        {selectedGame && (
          <GameDetail game={selectedGame} onBack={handleReset} />
        )}

        {/* Search results */}
        {!selectedGame && hasSearched && (
          <div className="mb-10">
            <SearchResults
              results={displayResults}
              searchTerm={searchTerm}
              onGameClick={handleGameClick}
            />
          </div>
        )}

        {/* Home: featured + trending */}
        {showHome && (
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
        )}

      </main>

      <Footer />
    </div>
  );
}

export default App;
