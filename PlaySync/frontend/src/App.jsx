import { useState, useEffect } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useGames } from './hooks/useGames';
import { gameService } from './services/api';
import { enrichWithMockData } from './utils/mockGameData';

import Navbar from './components/Common/Navbar';
import Footer from './components/Common/Footer';
import HomePage from './pages/HomePage';
import SearchPage from './pages/SearchPage';
import GamePage from './pages/GamePage';

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
    const uniqueId = appId || `fallback-${index}`;
    const capsule = appId ? steamCapsuleUrl(appId) : null;
    const tinyImage = normalizeImageUrl(item.tiny_image || item.img);
    return {
      id: uniqueId,
      title: item.name,
      coverImageUrl: capsule || tinyImage,
      backgroundImageUrl: capsule || tinyImage,
      tinyImageUrl: tinyImage,
      developer: 'Steam',
      offers: item.price
        ? [{ id: uniqueId, currentPrice: item.price.final || 0, originalPrice: item.price.initial || 0 }]
        : [{ id: uniqueId, currentPrice: 0, originalPrice: 0 }],
    };
  });
};

// ─── App ─────────────────────────────────────────────────────
function App() {
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
  const handleSearch = async (term) => {
    if (!term?.trim()) return;
    clearSearch();
    await search(term);
  };

  const handleGameClick = async (game) => {
    const needsPrices =
      !game.offers || game.offers.length === 0 || game.offers[0]?.currentPrice === 0;

    const resolvedSteamAppId = game.steamAppId || (typeof game.id === 'number' ? game.id : null);
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
      screenshots: [],
      screenshotsLoading: willFetchScreenshots,
    });

    const applyScreenshots = (screenshots) => {
      setSelectedGame((current) =>
        current ? { ...current, screenshots: screenshots?.length > 0 ? screenshots : current.screenshots, screenshotsLoading: false } : current
      );
    };

    if (activeSteamAppId) {
      gameService.getSteamScreenshots(activeSteamAppId).then(applyScreenshots).catch(() => applyScreenshots([]));
    } else if (game.rawgId) {
      gameService.getGameScreenshots(game.rawgId).then(applyScreenshots).catch(() => applyScreenshots([]));
    }
  };

  const handleLoadGame = async (id) => {
    setSelectedGame(null);
    const fromTrending = trending.find(g => String(g.id) === String(id));
    const fromFeatured = featured && String(featured.id) === String(id) ? featured : null;
    const cached = fromTrending || fromFeatured;
    if (cached) {
      await handleGameClick(cached);
      return;
    }
    // fallback para acesso direto pela URL com Steam App ID
    const numericId = Number(id);
    if (!isNaN(numericId) && numericId > 10000) {
      try {
        const results = await gameService.searchGames(String(id));
        if (results?.items?.length > 0) {
          const adapted = adaptSteamData({ items: results.items });
          const match = adapted.find(g => String(g.id) === String(id));
          if (match) {
            await handleGameClick(match);
            return;
          }
        }
      } catch { /* jogo não encontrado */ }
    }
  };

  // ── Derived state ─────────────────────────────────────────
  const displayResults = games.length > 0
    ? (() => {
        const seen = new Set();
        return adaptSteamData({ items: games }).filter(g => {
          const k = String(g.id);
          if (seen.has(k)) return false;
          seen.add(k);
          return true;
        });
      })()
    : [];

  // ── Render ────────────────────────────────────────────────
  return (
    <div className="min-h-screen bg-zinc-950 text-zinc-50 flex flex-col">
      <Navbar />

      <main className="flex-1 w-full max-w-6xl mx-auto px-4 sm:px-6 py-8 sm:py-12">
        <Routes>
          <Route
            path="/"
            element={
              <HomePage
                featured={featured}
                trending={trending}
                isLoading={isLoading}
                onGameClick={handleGameClick}
              />
            }
          />
          <Route
            path="/search"
            element={
              <SearchPage
                games={displayResults}
                isLoading={isLoading}
                error={error}
                onSearch={handleSearch}
                onGameClick={handleGameClick}
              />
            }
          />
          <Route
            path="/game/:id"
            element={
              <GamePage
                selectedGame={selectedGame}
                isLoading={isLoading}
                onLoadGame={handleLoadGame}
              />
            }
          />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>

      <Footer />
    </div>
  );
}

export default App;
