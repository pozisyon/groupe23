// App.tsx
import { Routes, Route, Link } from "react-router-dom";  // <-- garder seulement cette ligne
import Header from "./components/Header";

import GamePage from "./pages/GamePage";

export function App() {
  return (
    <div className="min-h-screen bg-gray-100">
      <Header />
      <Routes>
        <Route path="/" element={
          <div className="p-6">
            <h2 className="text-xl font-semibold mb-2">Accueil</h2>
            <div className="text-sm text-gray-600">Bienvenue sur Arbre32.</div>
            <div className="mt-4 flex gap-2">
              <Link to="/lobby" className="px-3 py-2 rounded bg-blue-600 text-white text-sm">Aller au lobby</Link>
              <Link to="/game" className="px-3 py-2 rounded border text-sm bg-white">Aller à la partie</Link>
            </div>
          </div>
        } />
        <Route path="/lobby" element={<LobbyPage />} />
        <Route path="/game" element={<GamePage />} />
      </Routes>
    </div>
  );
}

export default App;
