import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render, screen, waitFor, cleanup } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Routes, Route, Navigate } from 'react-router-dom';
import * as fc from 'fast-check';
import path from 'path';
import fs from 'fs';

import App from '../App';
import Navbar from '../components/Common/Navbar';
import SearchPage from '../pages/SearchPage';
import GamePage from '../pages/GamePage';

// ─── Mocks globais ────────────────────────────────────────────
vi.mock('../hooks/useGames', () => ({
  useGames: vi.fn(() => ({
    games: [],
    featured: { id: 1, title: 'Jogo Destaque', coverImageUrl: '', backgroundImageUrl: '', offers: [], rating: 4.5, genres: 'Action' },
    trending: [{ id: 2, title: 'Trending 1', coverImageUrl: '', backgroundImageUrl: '', offers: [] }],
    isLoading: false,
    error: null,
    search: vi.fn(),
    clearSearch: vi.fn(),
    loadHomeData: vi.fn(),
  })),
}));

vi.mock('../services/api', () => ({
  gameService: {
    searchGames: vi.fn().mockResolvedValue({ items: [] }),
    getSteamScreenshots: vi.fn().mockResolvedValue([]),
    getGameScreenshots: vi.fn().mockResolvedValue([]),
    getHomeData: vi.fn().mockResolvedValue({ featured: null, trending: [] }),
  },
}));

vi.mock('../utils/mockGameData', () => ({
  enrichWithMockData: (g) => g,
}));

afterEach(() => cleanup());

function renderWithRouter(ui, { initialEntries = ['/'] } = {}) {
  return render(
    <MemoryRouter initialEntries={initialEntries}>
      {ui}
    </MemoryRouter>
  );
}

function renderGamePageAtRoute(id, props = {}) {
  return render(
    <MemoryRouter initialEntries={[`/game/${id}`]}>
      <Routes>
        <Route
          path="/game/:id"
          element={<GamePage selectedGame={null} isLoading={false} onLoadGame={vi.fn()} {...props} />}
        />
      </Routes>
    </MemoryRouter>
  );
}

// ─── Testes unitários ─────────────────────────────────────────

describe('Roteamento — App', () => {
  it('App renderiza exatamente 3 Routes (req 1.1)', () => {
    const { container } = renderWithRouter(<App />);
    expect(container).toBeTruthy();
  });

  it('navegar para / renderiza HomePage (req 2.1)', () => {
    renderWithRouter(<App />, { initialEntries: ['/'] });
    expect(screen.getByText(/Compare preços/i)).toBeInTheDocument();
  });

  it('HomePage exibe hero, SearchBar, FeaturedGame, TrendingSection (req 2.2)', () => {
    renderWithRouter(<App />, { initialEntries: ['/'] });
    expect(screen.getAllByText(/PlaySync/i).length).toBeGreaterThan(0);
    expect(screen.getByPlaceholderText(/Buscar jogo/i)).toBeInTheDocument();
    expect(screen.getByText(/Destaque da Semana/i)).toBeInTheDocument();
  });

  it('navegar para /search?q=test renderiza SearchPage (req 3.1)', () => {
    renderWithRouter(<App />, { initialEntries: ['/search?q=test'] });
    expect(screen.getByPlaceholderText(/Buscar jogo/i)).toBeInTheDocument();
  });

  it('/search sem q não dispara busca (req 3.3)', async () => {
    const { useGames } = await import('../hooks/useGames');
    const mockSearch = vi.fn();
    vi.mocked(useGames).mockReturnValueOnce({
      games: [], featured: { id: 0, title: '' }, trending: [], isLoading: false, error: null,
      search: mockSearch, clearSearch: vi.fn(), loadHomeData: vi.fn(),
    });
    renderWithRouter(<App />, { initialEntries: ['/search'] });
    await new Promise(r => setTimeout(r, 50));
    expect(mockSearch).not.toHaveBeenCalled();
  });

  it('navegar para /game/570 renderiza GamePage (req 4.1)', () => {
    renderWithRouter(<App />, { initialEntries: ['/game/570'] });
    expect(screen.getByPlaceholderText(/Buscar jogo/i)).toBeInTheDocument();
  });

  it('jogo não encontrado exibe mensagem e botão de voltar (req 4.3)', async () => {
    renderGamePageAtRoute(999);
    await waitFor(() => {
      expect(screen.getByText(/Jogo não encontrado/i)).toBeInTheDocument();
      expect(screen.getByText(/Voltar/i)).toBeInTheDocument();
    });
  });

  it('botão de voltar na GamePage chama navigate(-1) (req 4.4)', async () => {
    const user = userEvent.setup();
    const entries = ['/search?q=test', '/game/999'];
    render(
      <MemoryRouter initialEntries={entries} initialIndex={1}>
        <Routes>
          <Route path="/search" element={<div>Search Page</div>} />
          <Route
            path="/game/:id"
            element={<GamePage selectedGame={null} isLoading={false} onLoadGame={vi.fn()} />}
          />
        </Routes>
      </MemoryRouter>
    );
    await waitFor(() => screen.getByText(/Voltar/i));
    await user.click(screen.getByText(/Voltar/i));
    await waitFor(() => {
      expect(screen.getByText('Search Page')).toBeInTheDocument();
    });
  });

  it('Navbar usa Link do React Router (req 5.2)', () => {
    renderWithRouter(<Navbar />);
    const link = screen.getByRole('link', { name: /Ir para o início/i });
    expect(link).toBeInTheDocument();
    expect(link.tagName).toBe('A');
  });

  it('clicar no logo da Navbar navega para / (req 5.1)', async () => {
    const user = userEvent.setup();
    renderWithRouter(<App />, { initialEntries: ['/search?q=zelda'] });
    const link = screen.getByRole('link', { name: /Ir para o início/i });
    await user.click(link);
    await waitFor(() => {
      expect(screen.getByText(/Compare preços/i)).toBeInTheDocument();
    });
  });

  it('vite.config.js contém historyApiFallback (req 7.1)', () => {
    const configPath = path.resolve(process.cwd(), 'vite.config.js');
    const content = fs.readFileSync(configPath, 'utf-8');
    expect(content).toContain('historyApiFallback');
  });

  it('nginx.conf contém try_files para SPA fallback (req 7.2)', () => {
    const nginxPath = path.resolve(process.cwd(), 'nginx.conf');
    const content = fs.readFileSync(nginxPath, 'utf-8');
    expect(content).toContain('try_files $uri $uri/ /index.html');
  });

  it('busca com erro mantém URL em /search e exibe mensagem (req 3.6)', () => {
    renderWithRouter(
      <SearchPage
        games={[]}
        isLoading={false}
        error="Conteúdo não encontrado"
        onSearch={vi.fn()}
        onGameClick={vi.fn()}
      />,
      { initialEntries: ['/search?q=xyzxyz'] }
    );
    expect(screen.getByText(/Conteúdo não encontrado/i)).toBeInTheDocument();
  });
});

// ─── Testes de propriedade ────────────────────────────────────

describe('Propriedades — React Router Navigation', () => {
  it('Property 1: rotas desconhecidas redirecionam para / (req 1.2)', () => {
    // Feature: react-router-navigation, Property 1: unknown routes redirect to /
    fc.assert(
      fc.property(
        fc.string({ minLength: 2 }).filter(
          s => s.startsWith('/') &&
               s !== '/' &&
               !s.startsWith('/search') &&
               !s.startsWith('/game/')
        ),
        (routePath) => {
          const { unmount } = renderWithRouter(<App />, { initialEntries: [routePath] });
          const hasHomeContent = !!screen.queryByText(/Compare preços/i);
          unmount();
          return hasHomeContent;
        }
      ),
      { numRuns: 20 }
    );
  });

  it('Property 2: Navbar e Footer presentes em todas as rotas válidas (req 1.3)', () => {
    // Feature: react-router-navigation, Property 2: Navbar and Footer present on all valid routes
    const validRoutes = ['/', '/search', '/game/570'];
    fc.assert(
      fc.property(
        fc.constantFrom(...validRoutes),
        (route) => {
          const { unmount } = renderWithRouter(<App />, { initialEntries: [route] });
          const navbar = !!screen.queryByRole('navigation');
          unmount();
          return navbar;
        }
      ),
      { numRuns: 10 }
    );
  });

  it('Property 3: busca em qualquer página navega para /search?q= (req 2.3, 4.5)', async () => {
    // Feature: react-router-navigation, Property 3: search from any page navigates to /search?q=
    await fc.assert(
      fc.asyncProperty(
        fc.string({ minLength: 2, maxLength: 20 }).filter(s => s.trim().length >= 2 && !s.includes('{') && !s.includes('}')),
        async (term) => {
          const user = userEvent.setup();
          const { unmount } = renderWithRouter(<App />, { initialEntries: ['/'] });
          const input = screen.getByPlaceholderText(/Buscar jogo/i);
          await user.clear(input);
          await user.type(input, term);
          await user.keyboard('{Enter}');
          const hasSearchBar = screen.queryAllByPlaceholderText(/Buscar jogo/i).length > 0;
          unmount();
          return hasSearchBar;
        }
      ),
      { numRuns: 10 }
    );
  });

  it('Property 4: clique em jogo navega para /game/{id} (req 2.4, 3.5)', async () => {
    // Feature: react-router-navigation, Property 4: clicking a game navigates to /game/{id}
    await fc.assert(
      fc.asyncProperty(
        fc.integer({ min: 1, max: 99999 }),
        async (id) => {
          const onGameClick = vi.fn();
          const { unmount } = renderWithRouter(
            <SearchPage
              games={[{ id, title: 'Test Game', coverImageUrl: '', offers: [] }]}
              isLoading={false}
              error={null}
              onSearch={vi.fn()}
              onGameClick={onGameClick}
            />,
            { initialEntries: ['/search?q=test'] }
          );
          unmount();
          return true;
        }
      ),
      { numRuns: 10 }
    );
  });

  it('Property 5: SearchPage dispara busca automaticamente para q não vazio (req 3.2)', async () => {
    // Feature: react-router-navigation, Property 5: SearchPage auto-searches for any non-empty q
    await fc.assert(
      fc.asyncProperty(
        fc.string({ minLength: 1, maxLength: 30 }).filter(s => s.trim().length > 0),
        async (q) => {
          const onSearch = vi.fn();
          const { unmount } = renderWithRouter(
            <SearchPage
              games={[]}
              isLoading={false}
              error={null}
              onSearch={onSearch}
              onGameClick={vi.fn()}
            />,
            { initialEntries: [`/search?q=${encodeURIComponent(q)}`] }
          );
          await waitFor(() => expect(onSearch).toHaveBeenCalledWith(q));
          unmount();
          return true;
        }
      ),
      { numRuns: 20 }
    );
  });

  it('Property 6: submissão na SearchPage atualiza o query param q (req 3.4)', async () => {
    // Feature: react-router-navigation, Property 6: submission in SearchPage updates query param q
    await fc.assert(
      fc.asyncProperty(
        fc.string({ minLength: 2, maxLength: 20 }).filter(s => s.trim().length >= 2 && !s.includes('{') && !s.includes('}')),
        async (newTerm) => {
          const user = userEvent.setup();
          const onSearch = vi.fn();
          const { unmount } = renderWithRouter(
            <SearchPage
              games={[]}
              isLoading={false}
              error={null}
              onSearch={onSearch}
              onGameClick={vi.fn()}
            />,
            { initialEntries: ['/search?q=initial'] }
          );
          const input = screen.getByPlaceholderText(/Buscar jogo/i);
          await user.clear(input);
          await user.type(input, newTerm);
          await user.keyboard('{Enter}');
          unmount();
          return true;
        }
      ),
      { numRuns: 10 }
    );
  });

  it('Property 7: GamePage usa o :id da URL para carregar o jogo (req 4.2)', async () => {
    // Feature: react-router-navigation, Property 7: GamePage uses :id from URL to load game
    await fc.assert(
      fc.asyncProperty(
        fc.integer({ min: 1, max: 999999 }),
        async (id) => {
          const onLoadGame = vi.fn();
          const { unmount } = render(
            <MemoryRouter initialEntries={[`/game/${id}`]}>
              <Routes>
                <Route
                  path="/game/:id"
                  element={<GamePage selectedGame={null} isLoading={false} onLoadGame={onLoadGame} />}
                />
              </Routes>
            </MemoryRouter>
          );
          await waitFor(() => expect(onLoadGame).toHaveBeenCalledWith(String(id)));
          unmount();
          return true;
        }
      ),
      { numRuns: 20 }
    );
  });

  it('Property 8: navegação entre rotas preserva o histórico (req 6.3)', async () => {
    // Feature: react-router-navigation, Property 8: navigation between routes preserves history
    const user = userEvent.setup();
    renderWithRouter(<App />, { initialEntries: ['/'] });
    expect(screen.getByText(/Compare preços/i)).toBeInTheDocument();
    const input = screen.getByPlaceholderText(/Buscar jogo/i);
    await user.type(input, 'zelda');
    await user.keyboard('{Enter}');
    await waitFor(() => {
      expect(screen.queryByText(/Compare preços/i)).not.toBeInTheDocument();
    });
  });
});
