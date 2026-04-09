import { Gamepad2 } from 'lucide-react';

function Navbar({ onReset }) {
  return (
    <nav className="sticky top-0 z-50 w-full backdrop-blur-md bg-zinc-950/85 border-b border-zinc-800/60">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 h-14 flex items-center">
        <button
          onClick={onReset}
          className="flex items-center gap-2.5 group"
          aria-label="Ir para o início"
        >
          <Gamepad2
            className="w-7 h-7 text-purple-400 group-hover:text-purple-300 transition-colors duration-200"
          />
          <span
            className="text-xl font-black font-display"
            style={{
              background: 'linear-gradient(135deg, #c4b5fd 0%, #818cf8 50%, #67e8f9 100%)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              backgroundClip: 'text',
            }}
          >
            PlaySync
          </span>
        </button>
      </div>
    </nav>
  );
}

export default Navbar;
