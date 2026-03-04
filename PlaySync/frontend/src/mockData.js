export const mockGameResult = {
  id: 1,
  title: "Tom Clancy's Rainbow Six Siege",
  description: "Domine a arte da destruição e do uso de gadgets em combates corpo a corpo altamente táticos, letalidade, decisões táticas e ação explosiva. Enfrente combates intensos em ambientes fechados neste FPS competitivo em equipes.",
  coverImageUrl: "https://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/359550/header.jpg",
  developer: "Ubisoft",
  
  // Nossas simulações de ofertas em lojas diferentes
  offers: [
    {
      id: 101,
      store: {
        id: 1,
        name: "Steam",
        logoUrl: "https://upload.wikimedia.org/wikipedia/commons/thumb/8/83/Steam_icon_logo.svg/512px-Steam_icon_logo.svg.png"
      },
      currentPrice: 59.99,
      historicalLowPrice: 23.99,
      dealUrl: "https://store.steampowered.com/app/359550/"
    },
    {
      id: 102,
      store: {
        id: 3,
        name: "Epic Games",
        logoUrl: "https://upload.wikimedia.org/wikipedia/commons/thumb/3/31/Epic_Games_logo.svg/512px-Epic_Games_logo.svg.png"
      },
      currentPrice: 85.99,
      historicalLowPrice: 19.99,
      dealUrl: "https://store.epicgames.com/pt-BR/p/rainbow-six-siege"
    },
    {
      id: 103,
      store: {
        id: 4,
        name: "Ubisoft Store",
        logoUrl: "https://upload.wikimedia.org/wikipedia/commons/thumb/7/78/Ubisoft_logo.svg/512px-Ubisoft_logo.svg.png"
      },
      currentPrice: 39.99,
      historicalLowPrice: 15.99,
      dealUrl: "https://store.ubisoft.com/pt/game?pid=56c494ad88a7e300458b4d5a"
    }
  ]
};