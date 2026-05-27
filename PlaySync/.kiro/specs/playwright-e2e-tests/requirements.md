# Requirements Document

## Introduction

Este documento define os requisitos para a suíte de testes E2E com Playwright no projeto PlaySync. O objetivo é complementar os testes unitários existentes (Vitest + Testing Library + fast-check) com testes de ponta a ponta que exercitem os fluxos reais de usuário no navegador, incluindo navegação entre páginas, busca de jogos, visualização de detalhes, estados de carregamento, tratamento de erros e responsividade básica.

A suíte cobre as três páginas da aplicação (HomePage em `/`, SearchPage em `/search?q=`, GamePage em `/game/:id`) e seus componentes principais (Navbar, Footer, SearchBar, FeaturedGame, TrendingSection, GameCard, GameDetail, PriceOffer), interagindo com o backend Spring Boot via interceptação de rede (mocking de API) para garantir testes determinísticos e independentes de disponibilidade externa.

## Glossary

- **Playwright_Suite**: O conjunto de testes E2E implementado com Playwright que valida os fluxos de usuário do PlaySync.
- **Page_Object**: Abstração de página usada nos testes para encapsular seletores e ações, seguindo o padrão Page Object Model.
- **API_Mock**: Interceptação de requisições de rede via `page.route()` do Playwright para retornar respostas controladas sem depender do backend real.
- **HomePage**: Página principal da aplicação, acessível pela rota `/`, contendo SearchBar, FeaturedGame e TrendingSection.
- **SearchPage**: Página de resultados de busca, acessível pela rota `/search?q=<termo>`, contendo SearchBar e SearchResults.
- **GamePage**: Página de detalhes de um jogo, acessível pela rota `/game/:id`, contendo GameDetail com preços e screenshots.
- **Navbar**: Componente de navegação fixo no topo com link para a HomePage.
- **Footer**: Componente de rodapé presente em todas as páginas.
- **SearchBar**: Componente de formulário com input de texto e botão de pesquisa presente em todas as páginas.
- **FeaturedGame**: Componente que exibe o jogo em destaque na HomePage.
- **TrendingSection**: Componente que exibe a grade de jogos populares na HomePage.
- **GameCard**: Componente de card de jogo exibido na TrendingSection e nos resultados de busca.
- **GameDetail**: Componente de detalhes completos de um jogo exibido na GamePage.
- **PriceOffer**: Componente que exibe uma oferta de preço de uma loja específica dentro do GameDetail.
- **Loading_State**: Estado visual de carregamento exibido enquanto dados são buscados da API.
- **Viewport_Mobile**: Viewport com largura de 375px (iPhone SE) usado nos testes de responsividade.
- **Viewport_Desktop**: Viewport com largura de 1280px usado nos testes de responsividade padrão.

---

## Requirements

### Requirement 1: Configuração e Infraestrutura da Suíte Playwright

**User Story:** Como desenvolvedor, quero uma suíte Playwright configurada e integrada ao projeto frontend, para que os testes E2E possam ser executados de forma isolada e reproduzível.

#### Acceptance Criteria

1. THE Playwright_Suite SHALL ser instalada como dependência de desenvolvimento no `package.json` do frontend com versão fixada.
2. THE Playwright_Suite SHALL incluir um arquivo `playwright.config.js` (ou `.ts`) na raiz do diretório frontend com configuração de `baseURL`, `testDir`, `timeout` e ao menos um projeto de browser (Chromium).
3. WHEN o comando `npx playwright test` é executado, THE Playwright_Suite SHALL executar todos os testes sem depender do backend real, utilizando API_Mock para interceptar todas as chamadas a `http://localhost:8080`.
4. THE Playwright_Suite SHALL incluir um script `"test:e2e"` no `package.json` do frontend que execute os testes Playwright.
5. WHERE o ambiente de CI é detectado, THE Playwright_Suite SHALL executar os testes no modo headless.
6. THE Playwright_Suite SHALL organizar os arquivos de teste no diretório `e2e/` dentro do frontend, separados dos testes unitários em `src/test/`.

---

### Requirement 2: Navegação entre Páginas

**User Story:** Como usuário, quero navegar entre a HomePage, SearchPage e GamePage de forma fluida, para que eu possa explorar jogos sem recarregamentos de página completos.

#### Acceptance Criteria

1. WHEN o usuário acessa a rota `/`, THE Playwright_Suite SHALL verificar que a HomePage é renderizada com o título "PlaySync", o SearchBar, a seção "Destaque da Semana" e a seção "Jogos Populares" visíveis.
2. WHEN o usuário clica no logo "PlaySync" na Navbar estando em qualquer página, THE Playwright_Suite SHALL verificar que a navegação retorna para a rota `/` e a HomePage é exibida.
3. WHEN o usuário acessa diretamente a rota `/search?q=zelda`, THE Playwright_Suite SHALL verificar que a SearchPage é renderizada com o SearchBar preenchido com "zelda".
4. WHEN o usuário acessa diretamente a rota `/game/570`, THE Playwright_Suite SHALL verificar que a GamePage é renderizada com o SearchBar visível.
5. WHEN o usuário acessa uma rota inexistente como `/pagina-invalida`, THE Playwright_Suite SHALL verificar que a aplicação redireciona para `/` e exibe a HomePage.
6. THE Playwright_Suite SHALL verificar que a Navbar está visível em todas as três rotas válidas (`/`, `/search?q=test`, `/game/570`).
7. THE Playwright_Suite SHALL verificar que o Footer está visível em todas as três rotas válidas.

---

### Requirement 3: Fluxo de Busca de Jogos

**User Story:** Como usuário, quero buscar jogos pelo nome usando o SearchBar em qualquer página, para que eu possa encontrar rapidamente o jogo que procuro.

#### Acceptance Criteria

1. WHEN o usuário digita um termo no SearchBar da HomePage e pressiona Enter, THE Playwright_Suite SHALL verificar que a URL muda para `/search?q=<termo>` e a SearchPage é exibida.
2. WHEN o usuário digita um termo no SearchBar da HomePage e clica no botão de pesquisa, THE Playwright_Suite SHALL verificar que a URL muda para `/search?q=<termo>` e a SearchPage é exibida.
3. WHEN o usuário está na SearchPage com resultados exibidos e digita um novo termo e pressiona Enter, THE Playwright_Suite SHALL verificar que a URL é atualizada para `/search?q=<novo-termo>`.
4. WHEN o usuário tenta submeter o SearchBar com o campo vazio, THE Playwright_Suite SHALL verificar que a URL não muda e nenhuma navegação ocorre.
5. WHEN a API_Mock retorna uma lista de jogos para um termo de busca, THE Playwright_Suite SHALL verificar que os GameCards correspondentes são exibidos na SearchPage.
6. WHEN a API_Mock retorna uma lista vazia para um termo de busca, THE Playwright_Suite SHALL verificar que a SearchPage exibe uma mensagem indicando ausência de resultados.
7. THE Playwright_Suite SHALL verificar que o SearchBar na SearchPage é pré-preenchido com o valor do parâmetro `q` da URL ao carregar a página.

---

### Requirement 4: Visualização de Detalhes de um Jogo

**User Story:** Como usuário, quero visualizar os detalhes completos de um jogo incluindo preços e screenshots, para que eu possa tomar uma decisão de compra informada.

#### Acceptance Criteria

1. WHEN o usuário clica em um GameCard na TrendingSection da HomePage, THE Playwright_Suite SHALL verificar que a navegação ocorre para `/game/<id>` e o GameDetail é exibido com o título do jogo.
2. WHEN o usuário clica em um GameCard nos resultados da SearchPage, THE Playwright_Suite SHALL verificar que a navegação ocorre para `/game/<id>` e o GameDetail é exibido.
3. WHEN a API_Mock retorna ofertas de preço para um jogo, THE Playwright_Suite SHALL verificar que a seção "Melhores Ofertas" exibe ao menos um PriceOffer com valor de preço visível.
4. WHEN a API_Mock não retorna ofertas de preço para um jogo, THE Playwright_Suite SHALL verificar que a seção "Melhores Ofertas" exibe a mensagem "Informações de preço não disponíveis" e o link para busca na Steam.
5. WHEN o usuário clica no botão "Voltar aos jogos" no GameDetail, THE Playwright_Suite SHALL verificar que a navegação retorna para a página anterior no histórico.
6. WHEN a API_Mock retorna screenshots para um jogo, THE Playwright_Suite SHALL verificar que a galeria de screenshots é exibida com ao menos uma imagem visível.
7. WHEN o usuário acessa `/game/<id>` diretamente e o jogo não é encontrado, THE Playwright_Suite SHALL verificar que a mensagem "Jogo não encontrado." e o botão "← Voltar" são exibidos.

---

### Requirement 5: Comportamento de Loading States

**User Story:** Como usuário, quero ver indicadores visuais de carregamento enquanto os dados são buscados, para que eu saiba que a aplicação está processando minha solicitação.

#### Acceptance Criteria

1. WHEN a HomePage está carregando dados iniciais, THE Playwright_Suite SHALL verificar que o skeleton de carregamento da FeaturedGame (elemento com `animate-pulse`) é exibido antes dos dados chegarem.
2. WHEN a HomePage está carregando dados iniciais, THE Playwright_Suite SHALL verificar que os skeletons de carregamento da TrendingSection são exibidos antes dos dados chegarem.
3. WHEN o usuário submete uma busca no SearchBar, THE Playwright_Suite SHALL verificar que o botão de pesquisa exibe o spinner de carregamento (elemento com `animate-spin`) enquanto a requisição está em andamento.
4. WHEN os dados da HomePage são carregados com sucesso, THE Playwright_Suite SHALL verificar que os skeletons são substituídos pelo conteúdo real (FeaturedGame com título e TrendingSection com GameCards).
5. WHEN a GamePage está carregando screenshots, THE Playwright_Suite SHALL verificar que o indicador "Carregando screenshots…" é exibido enquanto a requisição está em andamento.

---

### Requirement 6: Tratamento de Erros

**User Story:** Como usuário, quero receber feedback claro quando algo dá errado, para que eu entenda o problema e saiba como proceder.

#### Acceptance Criteria

1. WHEN a API_Mock retorna erro HTTP 500 para a busca de jogos, THE Playwright_Suite SHALL verificar que a SearchPage exibe uma mensagem de erro visível ao usuário.
2. WHEN a API_Mock retorna erro HTTP 404 para os dados da HomePage, THE Playwright_Suite SHALL verificar que a aplicação não trava e permanece funcional (sem tela em branco).
3. WHEN o usuário acessa `/game/<id>` com um ID que não corresponde a nenhum jogo conhecido e a API_Mock retorna lista vazia, THE Playwright_Suite SHALL verificar que a mensagem "Jogo não encontrado." é exibida.
4. WHEN a API_Mock simula timeout de rede para a busca, THE Playwright_Suite SHALL verificar que a SearchPage exibe uma mensagem de erro e o SearchBar permanece interativo.
5. IF a API_Mock retorna dados malformados (sem campo `items`), THEN THE Playwright_Suite SHALL verificar que a aplicação não lança exceção visível e exibe estado de erro ou lista vazia.

---

### Requirement 7: Responsividade Básica

**User Story:** Como usuário mobile, quero que a interface do PlaySync seja utilizável em dispositivos com tela pequena, para que eu possa buscar e comparar preços de jogos no celular.

#### Acceptance Criteria

1. WHEN o Playwright_Suite executa com Viewport_Mobile (375×667), THE Playwright_Suite SHALL verificar que a Navbar é visível e o logo "PlaySync" está acessível.
2. WHEN o Playwright_Suite executa com Viewport_Mobile, THE Playwright_Suite SHALL verificar que o SearchBar na HomePage é visível e interativo (input e botão de submit acessíveis).
3. WHEN o Playwright_Suite executa com Viewport_Mobile, THE Playwright_Suite SHALL verificar que a TrendingSection exibe os GameCards em grid de 2 colunas (classe `grid-cols-2`).
4. WHEN o Playwright_Suite executa com Viewport_Desktop (1280×720), THE Playwright_Suite SHALL verificar que a TrendingSection exibe os GameCards em grid de 5 colunas (classe `lg:grid-cols-5`).
5. WHEN o Playwright_Suite executa com Viewport_Mobile, THE Playwright_Suite SHALL verificar que o GameDetail exibe o layout em coluna única (sem grid lateral).
6. WHEN o Playwright_Suite executa com Viewport_Desktop, THE Playwright_Suite SHALL verificar que o GameDetail exibe o layout em grid de 4 colunas (`lg:grid-cols-4`) com sidebar e área principal.

---

### Requirement 8: Integração com Fluxo de Desenvolvimento

**User Story:** Como desenvolvedor, quero que os testes E2E sejam fáceis de manter e executar localmente, para que a suíte seja adotada como parte do processo de desenvolvimento.

#### Acceptance Criteria

1. THE Playwright_Suite SHALL incluir Page Objects para HomePage, SearchPage e GamePage que encapsulem seletores e ações reutilizáveis.
2. THE Playwright_Suite SHALL utilizar `data-testid` attributes nos componentes React quando seletores baseados em texto ou ARIA não forem suficientemente estáveis, documentando quais atributos foram adicionados.
3. WHEN um teste falha, THE Playwright_Suite SHALL capturar screenshot automático do estado da página no momento da falha (configurado via `screenshot: 'only-on-failure'`).
4. THE Playwright_Suite SHALL incluir um arquivo `README` ou comentários no `playwright.config.js` explicando como executar os testes localmente e como iniciar o servidor de desenvolvimento antes dos testes.
5. THE Playwright_Suite SHALL utilizar `beforeEach` hooks para configurar os API_Mocks necessários, garantindo isolamento entre testes.
6. FOR ALL testes que dependem de dados da API, THE Playwright_Suite SHALL utilizar fixtures JSON estáticas armazenadas em `e2e/fixtures/` para garantir determinismo e reprodutibilidade.

-------------------------------------------------------------------
-------------------------------------------------------------------
-------------------------------------------------------------------

Q1 (auto-resolved): The test suite runs with both mobile and desktop viewports during execution. Should GameDetail always use single column layout regardless of viewport, or should it adapt based on screen size?
A: GameDetail uses single column layout only on mobile viewport, adapts to grid layout on desktop

Q2: **Requirement 1.3:** The spec requires tests to run without depending on the backend when using API mocks, but should the system still maintain some backend dependency for configuration or initialization even when all API calls are intercepted?
A: Allow backend dependency even with full API mocking

Q3: **Requirement 1.3:** The spec requires backend independence through API mocking, but should partial mocking be allowed where some calls are intercepted while others still reach the real backend during development?
A: Require complete backend independence - all API calls must be mocked

Q4: **Requirement 2.3:** The spec requires the SearchPage to render when users access `/search?q=zelda`, but should the system also preload or render pages in the background before users navigate to them?
A: Allow background rendering - pages can be rendered without explicit user navigation for performance optimization.

Q5: **Requirement 3.5:** The spec requires GameCards to be displayed when the API returns games, but should the system still navigate to the SearchPage even if the GameCards fail to render?
A: Allow navigation even when GameCards fail to display

Q6: **Requirement 3.5:** The spec requires game cards to display when the API returns games, but what should happen if the API returns games but they fail to render due to technical issues like network timeouts or malformed data?
A: Treat rendering failures as test failures - the requirement is violated if game cards don't appear regardless of the technical cause.

Q7: **Requirement 5.1:** The spec requires skeleton loading indicators during initial data loading, but what should happen when data loads successfully while the system still considers itself in 'initial loading' state - should skeletons remain visible until the loading state fully completes, or disappear immediately when data arrives?
A: Keep skeletons visible until loading state fully completes

Q8: **Requirement 6.4:** The spec allows error messages to appear even when no search operation was initiated. Should error messages only display when an actual search fails?
A: Allow error messages to appear regardless of search state

Q9: **Requirement 6.5:** When the API returns malformed data, the spec requires both displaying an error state and preventing visible exceptions, but should the system prioritize graceful error display even if it means some technical errors might surface?
A: Require strict exception suppression - no visible exceptions even if error display is compromised