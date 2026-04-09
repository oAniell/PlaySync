import { ExternalLink, TrendingDown, Award } from 'lucide-react';

function PriceOffer({ offer, gameId, gameTitle }) {
  const isFree = offer.currentPrice === 0;
  const hasDiscount =
    !isFree && offer.originalPrice > 0 && offer.originalPrice > offer.currentPrice;
  const discountPct = hasDiscount
    ? Math.round((1 - offer.currentPrice / offer.originalPrice) * 100)
    : 0;
  const hasHistory = offer.historicalLowPrice > 0;
  const isHistoricalLow = hasHistory && offer.currentPrice <= offer.historicalLowPrice;

  const storeName = offer.store?.name ?? 'Steam';

  return (
    <div
      className={`
        bg-zinc-900 border rounded-xl p-4 sm:p-5
        flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4
        transition-all duration-200 hover:bg-zinc-900/70
        ${isHistoricalLow
          ? 'border-emerald-500/40 shadow-lg shadow-emerald-950/30'
          : 'border-zinc-800 hover:border-purple-500/30'}
      `}
    >
      {/* ── Store info ──────────────────────────────── */}
      <div className="flex items-center gap-3 min-w-0">
        {offer.store?.logoUrl && (
          <div className="w-12 h-12 bg-zinc-800 rounded-lg p-2 flex items-center justify-center shrink-0">
            <img
              src={offer.store.logoUrl}
              alt={storeName}
              className="w-full h-full object-contain"
              onError={(e) => { e.target.style.display = 'none'; }}
            />
          </div>
        )}
        <div className="min-w-0">
          <h4 className="font-bold truncate">{storeName}</h4>
          {hasHistory && (
            <p className="text-zinc-500 text-xs flex items-center gap-1 mt-0.5">
              <TrendingDown className="w-3 h-3 shrink-0" />
              Mínimo histórico:{' '}
              <span className="text-emerald-400 font-medium font-mono">
                R$ {offer.historicalLowPrice.toFixed(2)}
              </span>
            </p>
          )}
        </div>
      </div>

      {/* ── Price widget + action ───────────────────── */}
      <div className="flex items-center gap-3 sm:gap-4 shrink-0 justify-between sm:justify-end">

        {/* Price display — Steam style */}
        <div className="flex items-center gap-2.5">
          {hasDiscount && (
            /* Discount badge */
            <div className="bg-emerald-500 text-zinc-950 font-black text-sm px-2 py-1 rounded leading-none shrink-0">
              -{discountPct}%
            </div>
          )}

          <div className="text-right">
            {hasDiscount && (
              /* Original price crossed out */
              <p className="text-zinc-500 text-xs font-mono line-through leading-none mb-0.5">
                R$ {offer.originalPrice.toFixed(2)}
              </p>
            )}
            {/* Current price */}
            <p
              className={`font-extrabold font-mono leading-none ${
                isFree
                  ? 'text-emerald-300 text-xl'
                  : hasDiscount
                  ? 'text-emerald-400 text-2xl'
                  : 'text-zinc-100 text-2xl'
              }`}
            >
              {isFree ? 'Grátis' : `R$ ${offer.currentPrice.toFixed(2)}`}
            </p>
          </div>
        </div>

        {/* Historical low badge */}
        {isHistoricalLow && (
          <span className="hidden md:flex items-center gap-1 text-emerald-400 text-xs font-semibold">
            <Award className="w-3.5 h-3.5 shrink-0" />
            Menor preço!
          </span>
        )}

        {/* Buy button */}
        <a
          href={offer.dealUrl ?? `https://store.steampowered.com/app/${gameId}`}
          target="_blank"
          rel="noopener noreferrer"
          aria-label={offer.dealUrl ? `Comprar ${gameTitle}` : `Ver ${gameTitle} na Steam`}
          className="bg-purple-600 hover:bg-purple-500 text-white px-3.5 py-2.5 rounded-xl
                     transition-all duration-200 hover:shadow-lg hover:shadow-purple-900/40
                     flex items-center gap-2 text-sm font-semibold whitespace-nowrap"
        >
          <span>{offer.dealUrl ? 'Comprar' : 'Ver na Steam'}</span>
          <ExternalLink className="w-4 h-4 shrink-0" />
        </a>
      </div>
    </div>
  );
}

export default PriceOffer;
