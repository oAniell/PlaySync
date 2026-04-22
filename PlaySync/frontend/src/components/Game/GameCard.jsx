import { Star } from 'lucide-react';

const FALLBACK_IMAGE = 'https://images.unsplash.com/photo-1612287230217-8c7684717995?w=400&h=300&fit=crop';

function GameCard({ game, onClick, variant = 'search' }) {
  const imgHeight = variant === 'trending' ? 'h-40' : 'h-32';

  const lowestOffer =
    game.offers?.length > 0
      ? game.offers.reduce(
          (min, o) => (o.currentPrice < min.currentPrice ? o : min),
          game.offers[0]
        )
      : null;

  const subtitle =
    game.developer ||
    (typeof game.genres === 'string'
      ? game.genres.split(',')[0]?.trim()
      : game.genres?.[0]);

  return (
    <div
      role="button"
      tabIndex={0}
      onClick={() => onClick(game)}
      onKeyDown={(e) => e.key === 'Enter' && onClick(game)}
      className="bg-zinc-900 border border-zinc-800 rounded-xl overflow-hidden cursor-pointer
                 hover:border-blue-500/50 hover:scale-[1.03] hover:shadow-xl hover:shadow-blue-950/40
                 transition-all duration-300 group
                 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-blue-500 focus-visible:ring-offset-2 focus-visible:ring-offset-zinc-950"
    >
      {/* Image */}
      <div className="relative overflow-hidden">
        <img
          src={game.coverImageUrl || game.backgroundImageUrl || FALLBACK_IMAGE}
          alt={game.title}
          loading="lazy"
          className={`w-full ${imgHeight} object-cover group-hover:scale-105 transition-transform duration-500`}
          onError={(e) => {
            e.target.src = game.tinyImageUrl || FALLBACK_IMAGE;
            e.target.onerror = () => { e.target.src = FALLBACK_IMAGE; };
          }}
        />
        <div className="absolute inset-0 bg-gradient-to-t from-zinc-950 via-transparent to-transparent opacity-70" />

        {!!game.rating && (
          <div className="absolute top-2 right-2 bg-linear-to-r from-blue-600/90 to-cyan-600/90 backdrop-blur-sm text-white text-xs font-bold px-2 py-0.5 rounded-full flex items-center gap-1">
            <Star className="w-3 h-3 fill-current" />
            <span className="font-mono">{typeof game.rating === 'number' ? game.rating.toFixed(1) : game.rating}</span>
          </div>
        )}
      </div>

      {/* Content */}
      <div className="p-3">
        <h4 className="font-semibold text-sm truncate mb-1 group-hover:text-blue-400 transition-colors duration-200">
          {game.title}
        </h4>
        <div className="flex items-center justify-between gap-2">
          <span className="text-zinc-500 text-xs truncate">{subtitle}</span>
          {lowestOffer && (
            <span className="text-emerald-400 font-bold text-xs font-mono shrink-0">
              {lowestOffer.currentPrice === 0 ? 'Grátis' : `R$ ${lowestOffer.currentPrice.toFixed(2)}`}
            </span>
          )}
        </div>
      </div>
    </div>
  );
}

export default GameCard;
