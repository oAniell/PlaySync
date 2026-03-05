// Mock de dados para desenvolvimento do frontend
// Quando o backend estiver pronto, esses dados virão das APIs

// Lista de lojas disponíveis (simulando CheapShark)
export const mockStores = [
  { id: 1, name: "Steam", logoUrl: "https://upload.wikimedia.org/wikipedia/commons/thumb/8/83/Steam_icon_logo.svg/512px-Steam_icon_logo.svg.png" },
  { id: 2, name: "GOG", logoUrl: "https://upload.wikimedia.org/wikipedia/commons/thumb/2/21/GOG.com_logo.svg/512px-GOG.com_logo.svg.png" },
  { id: 3, name: "Epic Games", logoUrl: "https://upload.wikimedia.org/wikipedia/commons/thumb/3/31/Epic_Games_logo.svg/512px-Epic_Games_logo.svg.png" },
  { id: 4, name: "Ubisoft Store", logoUrl: "https://upload.wikimedia.org/wikipedia/commons/thumb/7/78/Ubisoft_logo.svg/512px-Ubisoft_logo.svg.png" },
  { id: 5, name: "Xbox Store", logoUrl: "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f9/Xbox_one_logo.svg/512px-Xbox_one_logo.svg.png" },
  { id: 7, name: "Humble Store", logoUrl: "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5f/Humble_Store_logo.png/512px-Humble_Store_logo.png" },
  { id: 11, name: "itch.io", logoUrl: "https://upload.wikimedia.org/wikipedia/commons/thumb/4/43/Itch.io_logo.svg/512px-Itch.io_logo.svg.png" },
  { id: 15, name: "Gamebillet", logoUrl: "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6f/Gamebillet_logo.png/512px-Gamebillet_logo.png" },
];

// Dados de jogos com preços de diferentes lojas
export const mockGames = [
  {
    id: 1,
    title: "Tom Clancy's Rainbow Six Siege",
    description: "Domine a arte da destruição e do uso de gadgets em combates corpo a corpo altamente táticos, letalidade, decisões táticas e ação explosiva. Enfrente combates intensos em ambientes fechados neste FPS competitivo em equipes.",
    coverImageUrl: "https://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/359550/header.jpg",
    backgroundImageUrl: "https://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/359550/library_hero.jpg",
    developer: "Ubisoft Montreal",
    publisher: "Ubisoft",
    genres: ["Ação", "Tático", "FPS"],
    releaseDate: "2015-12-01",
    rating: 4.5,
    offers: [
      {
        id: 101,
        store: mockStores[0],
        currentPrice: 59.99,
        historicalLowPrice: 23.99,
        dealUrl: "https://store.steampowered.com/app/359550/"
      },
      {
        id: 102,
        store: mockStores[2],
        currentPrice: 85.99,
        historicalLowPrice: 19.99,
        dealUrl: "https://store.epicgames.com/pt-BR/p/rainbow-six-siege"
      },
      {
        id: 103,
        store: mockStores[3],
        currentPrice: 39.99,
        historicalLowPrice: 15.99,
        dealUrl: "https://store.ubisoft.com/pt/game?pid=56c494ad88a7e300458b4d5a"
      }
    ]
  },
  {
    id: 2,
    title: "Grand Theft Auto V",
    description: "Um gangster sul-coreano, um gângster de Libertadores e um ex-militar-americano lutam por controle em uma sociedade filled de dinheiro sujo e conexões podres. Explore o mundo aberto masivo de Los Santos.",
    coverImageUrl: "https://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/271590/header.jpg",
    backgroundImageUrl: "https://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/271590/library_hero.jpg",
    developer: "Rockstar North",
    publisher: "Rockstar Games",
    genres: ["Ação", "Mundo Aberto", "RPG"],
    releaseDate: "2015-04-14",
    rating: 4.8,
    offers: [
      {
        id: 201,
        store: mockStores[0],
        currentPrice: 89.99,
        historicalLowPrice: 59.99,
        dealUrl: "https://store.steampowered.com/app/271590/"
      },
      {
        id: 202,
        store: mockStores[2],
        currentPrice: 99.90,
        historicalLowPrice: 49.90,
        dealUrl: "https://store.epicgames.com/pt-BR/p/grand-theft-auto-v"
      }
    ]
  },
  {
    id: 3,
    title: "Counter-Strike 2",
    description: "O maior jogo de tiro tático do mundo está de volta. Gráficos realistas, física avançada e a nova geração do motor Source. Competitivo, implacável e viciante.",
    coverImageUrl: "https://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/730/header.jpg",
    backgroundImageUrl: "https://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/730/library_hero.jpg",
    developer: "Valve",
    publisher: "Valve",
    genres: ["FPS", "Tático", "Competitivo"],
    releaseDate: "2023-09-27",
    rating: 4.6,
    offers: [
      {
        id: 301,
        store: mockStores[0],
        currentPrice: 0,
        historicalLowPrice: 0,
        dealUrl: "https://store.steampowered.com/app/730/"
      }
    ]
  },
  {
    id: 4,
    title: "The Witcher 3: Wild Hunt",
    description: "Você é Geralt de Rivia, um caçador de monstros pago. Em um continente devastado pela guerra e infestado de criaturas, sua história se desenrola em uma épica aventura非线性.",
    coverImageUrl: "https://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/292030/header.jpg",
    backgroundImageUrl: "https://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/292030/library_hero.jpg",
    developer: "CD Projekt RED",
    publisher: "CD Projekt",
    genres: ["RPG", "Ação", "Mundo Aberto"],
    releaseDate: "2015-05-18",
    rating: 4.9,
    offers: [
      {
        id: 401,
        store: mockStores[0],
        currentPrice: 79.99,
        historicalLowPrice: 39.99,
        dealUrl: "https://store.steampowered.com/app/292030/"
      },
      {
        id: 402,
        store: mockStores[1],
        currentPrice: 39.99,
        historicalLowPrice: 29.99,
        dealUrl: "https://www.gog.com/en/game/the_witcher_3_wild_hunt_game_of_the_year_edition"
      },
      {
        id: 403,
        store: mockStores[3],
        currentPrice: 89.90,
        historicalLowPrice: 44.90,
        dealUrl: "https://store.ubisoft.com/pt/game?pid=e090d258-a8b3-4d38-9a1e-0d502543c307"
      }
    ]
  },
  {
    id: 5,
    title: "Sea of Thieves",
    description: "Voe solitário ou ajunte sua tripulação e zarpe em uma aventura共享. Sea of Thieves oferece a experiência de pirata definitiva, com mundo aberto, cooperation e saques.",
    coverImageUrl: "https://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/1172620/header.jpg",
    backgroundImageUrl: "https://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/1172620/library_hero.jpg",
    developer: "Rare Ltd",
    publisher: "Microsoft",
    genres: ["Ação", "Aventura", "Multijogador"],
    releaseDate: "2022-06-03",
    rating: 4.3,
    offers: [
      {
        id: 501,
        store: mockStores[0],
        currentPrice: 119.99,
        historicalLowPrice: 59.99,
        dealUrl: "https://store.steampowered.com/app/1172620/"
      },
      {
        id: 502,
        store: mockStores[4],
        currentPrice: 149.90,
        historicalLowPrice: 74.90,
        dealUrl: "https://www.xbox.com/pt-BR/games/sea-of-thieves"
      }
    ]
  },
  {
    id: 6,
    title: "Elden Ring",
    description: "Levante-se, Maculado, e seja guiado pela graça para portar o poder do Anel Ancião e se tornar um Lorde Elden nas Terras Intermediárias, um mundo sombrio moldado pela obscuridade.",
    coverImageUrl: "https://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/1245620/header.jpg",
    backgroundImageUrl: "https://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/1245620/library_hero.jpg",
    developer: "FromSoftware",
    publisher: "Bandai Namco",
    genres: ["RPG", "Ação", "Souls-like"],
    releaseDate: "2022-02-25",
    rating: 4.9,
    offers: [
      {
        id: 601,
        store: mockStores[0],
        currentPrice: 199.99,
        historicalLowPrice: 119.99,
        dealUrl: "https://store.steampowered.com/app/1245620/"
      },
      {
        id: 602,
        store: mockStores[2],
        currentPrice: 239.90,
        historicalLowPrice: 159.90,
        dealUrl: "https://store.epicgames.com/pt-BR/p/elden-ring"
      }
    ]
  },
  {
    id: 7,
    title: "Valorant",
    description: "Valorant é um jogo de tiro tático 5v5 baseado em personagens. Precisão, habilidade e trabalho em equipe são sua arma. Domine agentes únicos e conquiste o competitivo.",
    coverImageUrl: "https://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/1332010/header.jpg",
    backgroundImageUrl: "https://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/1332010/library_hero.jpg",
    developer: "Riot Games",
    publisher: "Riot Games",
    genres: ["FPS", "Tático", "Competitivo"],
    releaseDate: "2020-06-02",
    rating: 4.4,
    offers: [
      {
        id: 701,
        store: mockStores[0],
        currentPrice: 0,
        historicalLowPrice: 0,
        dealUrl: "https://store.steampowered.com/app/1332010/"
      }
    ]
  },
  {
    id: 8,
    title: "Cyberpunk 2077",
    description: "Cyberpunk 2077 é uma história de ação e aventura em mundo aberto, ambientada na megalópole de Night City, onde você é um cyberpunk engajado em uma luta pela sobrevivência.",
    coverImageUrl: "https://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/1091500/header.jpg",
    backgroundImageUrl: "https://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/1091500/library_hero.jpg",
    developer: "CD Projekt RED",
    publisher: "CD Projekt",
    genres: ["RPG", "FPS", "Mundo Aberto"],
    releaseDate: "2020-12-10",
    rating: 4.2,
    offers: [
      {
        id: 801,
        store: mockStores[0],
        currentPrice: 149.99,
        historicalLowPrice: 89.99,
        dealUrl: "https://store.steampowered.com/app/1091500/"
      },
      {
        id: 802,
        store: mockStores[1],
        currentPrice: 99.99,
        historicalLowPrice: 79.99,
        dealUrl: "https://www.gog.com/en/game/cyberpunk_2077"
      },
      {
        id: 803,
        store: mockStores[2],
        currentPrice: 179.90,
        historicalLowPrice: 119.90,
        dealUrl: "https://store.epicgames.com/pt-BR/p/cyberpunk-2077"
      }
    ]
  },
  {
    id: 9,
    title: "Baldur's Gate 3",
    description: "Reunite seu grupo e retorne às Terras Esquecidas em uma história de companionship e betrayal, sacrifício e sobrevivência. Um RPG de mundo aberto baseado em D&D.",
    coverImageUrl: "https://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/1086940/header.jpg",
    backgroundImageUrl: "https://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/1086940/library_hero.jpg",
    developer: "Larian Studios",
    publisher: "Larian Studios",
    genres: ["RPG", "Estratégia", "Turn-Based"],
    releaseDate: "2023-08-03",
    rating: 4.9,
    offers: [
      {
        id: 901,
        store: mockStores[0],
        currentPrice: 189.99,
        historicalLowPrice: 149.99,
        dealUrl: "https://store.steampowered.com/app/1086940/"
      },
      {
        id: 902,
        store: mockStores[1],
        currentPrice: 179.99,
        historicalLowPrice: 159.99,
        dealUrl: "https://www.gog.com/en/game/baldurs_gate_3"
      }
    ]
  },
  {
    id: 10,
    title: "Minecraft",
    description: "Crie, explore e sobreviva em um mundo gerado aleatoriamente. Desde建造 simples até masmorras complexas, deixe sua imaginação fluir neste sandbox ilimitado.",
    coverImageUrl: "QUEBRAhttps://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/1258090/header.jpg",
    backgroundImageUrl: "QUEBRAhttps://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/1258090/library_hero.jpg",
    developer: "Mojang Studios",
    publisher: "Microsoft",
    genres: ["Sandbox", "Sobrevivência", "Construção"],
    releaseDate: "2011-11-18",
    rating: 4.7,
    offers: [
      {
        id: 1001,
        store: mockStores[0],
        currentPrice: 109.99,
        historicalLowPrice: 79.99,
        dealUrl: "https://store.steampowered.com/app/1091500/"
      }
    ]
  }
];

// Jogo em destaque (para exibir naホーム)
export const featuredGame = mockGames[0];

// Jogos em destaque (top 5)
export const trendingGames = mockGames.slice(0, 5);

// Export individual para compatibilidade retroativa
export const mockGameResult = mockGames[0];

// Função auxiliar para encontrar jogo por ID
export const getGameById = (id) => mockGames.find(game => game.id === id);

// Função auxiliar para buscar jogos por termo
export const searchGames = (term) => {
  const lowerTerm = term.toLowerCase();
  return mockGames.filter(game => 
    game.title.toLowerCase().includes(lowerTerm) ||
    game.developer.toLowerCase().includes(lowerTerm) ||
    game.genres.some(genre => genre.toLowerCase().includes(lowerTerm))
  );
};
