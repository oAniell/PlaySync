import { Star, Sparkles, ArrowRight } from 'lucide-react';

const FALLBACK_IMAGE = 'https://images.unsplash.com/photo-1612287230217-8c7684717995?w=800&h=450&fit=crop';

function FeaturedGame({ featured, onClick, isLoading }) {
  if (isLoading) {
    return (
      <div className="w-full">
        <div className="flex items-center gap-2 mb-4">
          <Sparkles className="w-5 h-5 text-yellow-400" />
          <h3 className="text-lg font-bold font-display">Destaque da Semana</h3>
        </div>
        <div className="w-full h-72 md:h-96 rounded-2xl bg-zinc-900 animate-pulse" />
      </div>
    );
  }

  if (!featured?.title) return null;

  const genres =
    typeof featured.genres === 'string'
      ? featured.genres.split(',').map((g) => g.trim()).filter(Boolean).slice(0, 3)
      : Array.isArray(featured.genres)
      ? featured.genres.slice(0, 3)
      : [];

  return (
    <div className="w-full">
      <div className="flex items-center gap-2 mb-4">
        <Sparkles className="w-5 h-5 text-yellow-400" />
        <h3 className="text-lg font-bold font-display">Destaque da Semana</h3>
      </div>

      <div
        role="button"
        tabIndex={0}
        onClick={() => onClick(featured)}
        onKeyDown={(e) => e.key === 'Enter' && onClick(featured)}
        className="relative w-full h-72 md:h-96 rounded-2xl overflow-hidden cursor-pointer group
                   focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-blue-500
                   focus-visible:ring-offset-2 focus-visible:ring-offset-zinc-950"
      >
        {/* Background */}
        <img
          src={featured.backgroundImageUrl || featured.coverImageUrl || FALLBACK_IMAGE}
          alt={featured.title}
          className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-700 ease-out"
          onError={(e) => { e.target.src = FALLBACK_IMAGE; }}
        />

        {/* Gradients */}
        <div className="absolute inset-0 bg-gradient-to-t from-zinc-950 via-zinc-950/50 to-transparent" />
        <div className="absolute inset-0 bg-gradient-to-r from-zinc-950/80 via-zinc-950/20 to-transparent" />

        {/* Content */}
        <div className="absolute bottom-0 left-0 right-0 p-6 md:p-8">
          {/* Badges row */}
          <div className="flex items-center gap-2 mb-3 flex-wrap">
            <span className="bg-yellow-500 text-zinc-950 text-xs font-black px-2.5 py-1 rounded uppercase tracking-wider">
              Destaque
            </span>
            {genres.map((g, i) => (
              <span
                key={i}
                className="text-zinc-200 text-xs font-medium bg-zinc-900/70 backdrop-blur-sm px-2.5 py-0.5 rounded-full hidden sm:inline-block"
              >
                {g}
              </span>
            ))}
          </div>

          {/* Title */}
          <h2 className="text-3xl md:text-5xl font-black font-display mb-4 leading-tight tracking-tight">
            {featured.title}
          </h2>

          {/* Bottom row */}
          <div className="flex items-center gap-5">
            {!!featured.rating && (
              <div className="flex items-center gap-1.5">
                <Star className="w-5 h-5 text-yellow-400 fill-yellow-400" />
                <span className="text-yellow-400 font-bold text-xl font-mono">
                  {typeof featured.rating === 'number' ? featured.rating.toFixed(1) : featured.rating}
                </span>
              </div>
            )}
            <div className="flex items-center gap-2 text-sm font-semibold text-blue-400 group-hover:text-blue-300 transition-colors">
              <span>Ver preços</span>
              <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform duration-200" />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default FeaturedGame;
