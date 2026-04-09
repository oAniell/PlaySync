import { Search } from 'lucide-react';

function SearchBar({ value, onChange, onSubmit, isLoading }) {
  return (
    <form onSubmit={onSubmit} className="w-full max-w-2xl">
      <div className="relative flex items-center">
        <input
          type="text"
          value={value}
          onChange={(e) => onChange(e.target.value)}
          placeholder="Buscar jogo... Ex: Elden Ring, Cyberpunk 2077..."
          autoComplete="off"
          aria-label="Buscar jogo"
          className="w-full bg-zinc-900 border border-zinc-700 rounded-full py-4 pl-6 pr-14 text-base
                     text-zinc-100 placeholder-zinc-500
                     focus:outline-none focus:border-purple-500 focus:ring-1 focus:ring-purple-500/40
                     transition-all duration-200 shadow-lg"
        />
        <button
          type="submit"
          disabled={isLoading}
          aria-label="Pesquisar"
          className="absolute right-2 inset-y-2 bg-purple-600 hover:bg-purple-500
                     disabled:bg-purple-900 disabled:cursor-not-allowed
                     text-white rounded-full px-4 min-w-[44px]
                     transition-all duration-200 hover:shadow-lg hover:shadow-purple-900/40
                     flex items-center justify-center"
        >
          {isLoading ? (
            <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />
          ) : (
            <Search className="w-5 h-5" />
          )}
        </button>
      </div>
    </form>
  );
}

export default SearchBar;
