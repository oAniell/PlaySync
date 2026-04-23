/**
 * Mock data for the game detail page.
 * TODO: replace with real backend API integration (screenshots, description, etc.)
 */

const MOCK_SCREENSHOTS = [
  'https://images.unsplash.com/photo-1542751371-adc38448a05e?w=900&h=506&fit=crop',
  'https://images.unsplash.com/photo-1511512578047-dfb367046420?w=900&h=506&fit=crop',
  'https://images.unsplash.com/photo-1493711662062-fa541adb3fc8?w=900&h=506&fit=crop',
  'https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=900&h=506&fit=crop',
  'https://images.unsplash.com/photo-1585155770447-2f66e2a397b5?w=900&h=506&fit=crop',
  'https://images.unsplash.com/photo-1519326844852-704caea5679e?w=900&h=506&fit=crop',
  'https://images.unsplash.com/photo-1598550476439-6847785fcea6?w=900&h=506&fit=crop',
  'https://images.unsplash.com/photo-1612287230217-8c7684717995?w=900&h=506&fit=crop',
  'https://images.unsplash.com/photo-1580327344181-c1163234e5a0?w=900&h=506&fit=crop',
  'https://images.unsplash.com/photo-1563379926898-05f4575a45d8?w=900&h=506&fit=crop',
];

const MOCK_DESCRIPTIONS = [
  'Um épico jogo de ação e aventura que coloca você no centro de um mundo vasto e imersivo. Explore cenários deslumbrantes, enfrente desafios únicos e descubra uma narrativa profunda que vai te prender por dezenas de horas.',
  'Mergulhe em uma experiência de jogo sem igual, com mecânicas inovadoras e um universo rico em detalhes. Tome decisões que moldam o destino dos personagens em uma história repleta de reviravoltas inesperadas.',
  'Uma obra-prima do gênero, com gráficos de última geração e jogabilidade viciante. Combine estratégia e reflexos para superar inimigos formidáveis neste título aclamado pela crítica especializada.',
  'Embarque em uma jornada épica por terras desconhecidas. Forje alianças, desvende segredos milenares e enfrente adversários implacáveis neste título que redefine os padrões do gênero.',
  'Prepare-se para uma aventura sem precedentes. Com um mundo aberto repleto de missões, personagens memoráveis e batalhas intensas, este é um dos títulos mais completos e elogiados da geração.',
];

// Discount rates applied cyclically when a game doesn't have real discount data
const MOCK_DISCOUNT_RATES = [0.50, 0.35, 0.20, 0.60, 0.25, 0.40, 0.15, 0.30];

/**
 * Enriches a game object with mock screenshots, description and discount data.
 * Data tagged with `_mock: true` so it can be stripped when real API is ready.
 */
export function enrichWithMockData(game) {
  const seed = typeof game.id === 'number' ? game.id : parseInt(String(game.id), 10) || 0;

  // Pick 4 screenshots cycling through the pool
  const offset = seed % (MOCK_SCREENSHOTS.length - 3);
  const screenshots = game.screenshots ?? MOCK_SCREENSHOTS.slice(offset, offset + 4);

  // Description
  const description = game.description || MOCK_DESCRIPTIONS[seed % MOCK_DESCRIPTIONS.length];

  // Enrich offer prices: add mock originalPrice when discount data is missing
  const offers = (game.offers ?? []).map((offer, i) => {
    const alreadyDiscounted =
      offer.originalPrice > 0 && offer.originalPrice > offer.currentPrice;
    if (alreadyDiscounted || offer.currentPrice === 0) return offer;

    const rate = MOCK_DISCOUNT_RATES[(seed + i) % MOCK_DISCOUNT_RATES.length];
    return {
      ...offer,
      originalPrice: parseFloat((offer.currentPrice / (1 - rate)).toFixed(2)),
      _mockDiscount: true, // flag for easy removal later
    };
  });

  return { ...game, description, screenshots, offers, _mockEnriched: true };
}
