import { useState } from 'react';
import { Star, TrendingDown, ExternalLink, ChevronLeft, Image } from 'lucide-react';
import PriceOffer from './PriceOffer';

const FALLBACK_IMAGE = 'https://images.unsplash.com/photo-1612287230217-8c7684717995?w=400&h=300&fit=crop';

/* ── Screenshots Gallery ─────────────────────────────────── */
function ScreenshotsGallery({ screenshots }) {
  const [active, setActive] = useState(0);

  if (!screenshots?.length) return null;

  return (
    <div className="bg-zinc-900 border border-zinc-800 rounded-xl overflow-hidden">
      {/* Main image */}
      <div className="relative w-full aspect-video bg-zinc-950">
        <img
          key={active}
          src={screenshots[active]}
          alt={`Screenshot ${active + 1}`}
          className="w-full h-full object-cover animate-fade-in"
          onError={(e) => { e.target.src = FALLBACK_IMAGE; }}
        />
        {/* Image counter */}
        <div className="absolute top-2 right-2 bg-zinc-950/70 backdrop-blur-sm text-zinc-400 text-xs px-2 py-0.5 rounded-full font-mono">
          {active + 1}/{screenshots.length}
        </div>
      </div>

      {/* Thumbnails */}
      <div className="flex gap-2 p-2.5 overflow-x-auto scrollbar-none">
        {screenshots.map((src, i) => (
          <button
            key={i}
            onClick={() => setActive(i)}
            className={`shrink-0 w-24 h-14 rounded overflow-hidden border-2 transition-all duration-150
              ${i === active
                ? 'border-purple-500 opacity-100'
                : 'border-zinc-700 opacity-60 hover:opacity-90 hover:border-zinc-500'
              }`}
          >
            <img
              src={src}
              alt={`Thumb ${i + 1}`}
              className="w-full h-full object-cover"
              onError={(e) => { e.target.src = FALLBACK_IMAGE; }}
            />
          </button>
        ))}
      </div>
    </div>
  );
}

/* ── Main component ──────────────────────────────────────── */
function GameDetail({ game, onBack }) {
  const genres =
    typeof game.genres === 'string'
      ? game.genres.split(',').map((g) => g.trim()).filter(Boolean).slice(0, 5)
      : Array.isArray(game.genres)
      ? game.genres.slice(0, 5)
      : [];

  const screenshots = game.screenshots ?? [];

  return (
    <div className="w-full grid grid-cols-1 lg:grid-cols-4 gap-6 animate-slide-up">

      {/* ── Left sidebar: Game Info ───────────────── */}
      <div className="lg:col-span-1">
        <div className="bg-zinc-900 border border-zinc-800 rounded-2xl overflow-hidden shadow-2xl lg:sticky lg:top-20">

          {/* Cover */}
          <div className="relative">
            <img
              src={game.coverImageUrl || game.backgroundImageUrl || FALLBACK_IMAGE}
              alt={game.title}
              className="w-full h-56 object-cover"
              onError={(e) => { e.target.src = FALLBACK_IMAGE; }}
            />
            <div className="absolute inset-0 bg-gradient-to-t from-zinc-900 via-zinc-900/20 to-transparent" />

            {!!game.rating && (
              <div className="absolute top-3 right-3 bg-purple-600/90 backdrop-blur-sm text-white text-xs font-bold px-2.5 py-1 rounded-full flex items-center gap-1.5">
                <Star className="w-3.5 h-3.5 fill-current" />
                <span className="font-mono">
                  {typeof game.rating === 'number' ? game.rating.toFixed(1) : game.rating}
                </span>
              </div>
            )}
          </div>

          {/* Info */}
          <div className="p-5">
            <h2 className="text-lg font-bold font-display leading-tight line-clamp-2 mb-3">
              {game.title}
            </h2>

            {/* Genres */}
            {genres.length > 0 && (
              <div className="flex flex-wrap gap-1.5 mb-4">
                {genres.map((g, i) => (
                  <span
                    key={i}
                    className="text-xs font-medium bg-purple-950/60 text-purple-300 border border-purple-800/50 px-2 py-0.5 rounded-full"
                  >
                    {g}
                  </span>
                ))}
              </div>
            )}

            {/* Meta fields */}
            <div className="space-y-2.5 mb-4 text-sm">
              {game.developer && (
                <div>
                  <span className="text-zinc-600 text-xs uppercase tracking-wider">Desenvolvedora</span>
                  <p className="text-zinc-300 mt-0.5">{game.developer}</p>
                </div>
              )}
              {game.publisher && (
                <div>
                  <span className="text-zinc-600 text-xs uppercase tracking-wider">Editora</span>
                  <p className="text-zinc-300 mt-0.5">{game.publisher}</p>
                </div>
              )}
              {game.platforms && (
                <div>
                  <span className="text-zinc-600 text-xs uppercase tracking-wider">Plataformas</span>
                  <p className="text-zinc-300 mt-0.5">{game.platforms}</p>
                </div>
              )}
            </div>

            {/* Description (sidebar – truncated) */}
            {game.description && (
              <p className="text-zinc-500 text-xs leading-relaxed line-clamp-5 mb-4 border-t border-zinc-800 pt-3">
                {game.description}
              </p>
            )}

            <button
              onClick={onBack}
              className="w-full flex items-center justify-center gap-2
                         bg-zinc-800 hover:bg-zinc-700 text-zinc-300 hover:text-white
                         py-2.5 rounded-lg transition-all duration-200 text-sm font-medium"
            >
              <ChevronLeft className="w-4 h-4" />
              Voltar aos jogos
            </button>
          </div>
        </div>
      </div>

      {/* ── Right: Screenshots + Description + Prices ── */}
      <div className="lg:col-span-3 flex flex-col gap-5">

        {/* Price offers */}
        <div className="flex flex-col gap-3">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 bg-emerald-950/60 rounded-lg flex items-center justify-center">
              <TrendingDown className="w-4 h-4 text-emerald-400" />
            </div>
            <h3 className="text-lg font-bold font-display">Melhores Ofertas</h3>
            {game.offers?.length > 0 && (
              <span className="text-xs font-medium bg-zinc-800 text-zinc-400 px-2 py-0.5 rounded-full">
                {game.offers.length} {game.offers.length === 1 ? 'loja' : 'lojas'}
              </span>
            )}
          </div>

          {game.offers?.length > 0 ? (
            game.offers.map((offer, i) => (
              <PriceOffer
                key={offer.id ?? i}
                offer={offer}
                gameId={game.id}
                gameTitle={game.title}
              />
            ))
          ) : (
            <div className="bg-zinc-900 border border-zinc-800 rounded-xl p-10 text-center">
              <div className="w-12 h-12 bg-zinc-800 rounded-xl flex items-center justify-center mx-auto mb-4">
                <TrendingDown className="w-6 h-6 text-zinc-600" />
              </div>
              <p className="text-zinc-400 text-sm mb-5">Informações de preço não disponíveis</p>
              <a
                href={`https://store.steampowered.com/search/?term=${encodeURIComponent(game.title)}`}
                target="_blank"
                rel="noopener noreferrer"
                className="bg-purple-600 hover:bg-purple-500 text-white px-5 py-2.5 rounded-xl
                           transition-all duration-200 hover:shadow-lg hover:shadow-purple-900/40
                           inline-flex items-center gap-2 text-sm font-medium"
              >
                Buscar na Steam
                <ExternalLink className="w-4 h-4" />
              </a>
            </div>
          )}
        </div>

        {/* Description */}
        {game.description && (
          <div className="bg-zinc-900 border border-zinc-800 rounded-xl p-5">
            <h4 className="text-xs font-bold uppercase tracking-wider text-zinc-500 mb-2">Sobre o jogo</h4>
            <p className="text-zinc-300 text-sm leading-relaxed">{game.description}</p>
          </div>
        )}

        {/* Screenshots gallery */}
        {screenshots.length > 0 ? (
          <ScreenshotsGallery screenshots={screenshots} />
        ) : (
          <div className="w-full aspect-video bg-zinc-900 border border-zinc-800 rounded-xl
                          flex flex-col items-center justify-center gap-3 text-zinc-700">
            <Image className="w-10 h-10" />
            <span className="text-sm">Screenshots disponíveis em breve</span>
          </div>
        )}

      </div>
    </div>
  );
}

export default GameDetail;
