import { Link } from "react-router-dom";

export default function Header() {
  return (
    <header className="w-full h-16 bg-white border-b flex items-center px-4 gap-6">
      <div className="w-8 h-8 rounded-lg bg-blue-600" />
      <h1 className="font-semibold">Arbre32</h1>
      <nav className="ml-auto flex gap-4 text-sm">
        <Link to="/">Accueil</Link>
        <Link to="/lobby">Lobby</Link>
        <Link to="/game">Partie</Link>
      </nav>
    </header>
  );
}
