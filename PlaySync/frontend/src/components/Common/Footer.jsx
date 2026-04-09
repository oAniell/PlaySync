function Footer() {
  return (
    <footer className="mt-20 pb-8 text-center text-sm border-t border-zinc-900 pt-8">
      <p className="text-zinc-700 mb-1">© 2026 PlaySync — Comparador de preços de jogos</p>
      <p className="text-zinc-600">
        Dados fornecidos por{' '}
        <a
          href="https://www.cheapshark.com"
          target="_blank"
          rel="noopener noreferrer"
          className="text-purple-600 hover:text-purple-400 transition-colors"
        >
          CheapShark
        </a>
        {' '}e{' '}
        <a
          href="https://rawg.io"
          target="_blank"
          rel="noopener noreferrer"
          className="text-purple-600 hover:text-purple-400 transition-colors"
        >
          RAWG
        </a>
      </p>
    </footer>
  );
}

export default Footer;
