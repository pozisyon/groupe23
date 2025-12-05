import { useEffect, useState } from "react";
import { useGame } from "../context/GameContext";
import { api } from "../api/http";
import { useSearchParams } from "react-router-dom";

interface OpenGame {
  id: string;
  players?: number;
  status?: string;
}

export default function LobbyPage() {
  const { initNewGame, joinGame } = useGame();

  const [waiting, setWaiting] = useState<OpenGame[]>([]);
  const [loading, setLoading] = useState(false);
  const [manualId, setManualId] = useState("");

  const [params] = useSearchParams();

  useEffect(() => {
    const newId = params.get("new");
    if (newId) setManualId(newId);
  }, [params]);

  useEffect(() => {
    const load = async () => {
      try {
        const res = await api.get("/api/game/open");
        setWaiting(res.data);
      } catch (e) {
        console.error("Erreur:", e);
      }
    };

    load();
    const t = setInterval(load, 5000);
    return () => clearInterval(t);
  }, []);

  const handleJoin = async (id: string) => {
    if (!id) return;
    setLoading(true);
    try {
      await joinGame(id);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen p-6 bg-gradient-to-b from-green-50 to-green-100">
      <div className="max-w-2xl mx-auto space-y-10">

        {/* HEADER */}
        <div className="text-center space-y-2">
          <h1 className="text-3xl font-extrabold text-green-800">🎮 Lobby Arbre32</h1>
          <p className="text-gray-700 text-sm">
            Rejoignez une partie, créez-en une nouvelle, ou entrez un ID partagé par un ami.
          </p>
        </div>

        {/* INSTRUCTIONS */}
        <div className="p-4 bg-white border border-green-300 rounded-xl shadow">
          <h2 className="text-lg font-semibold text-green-700 mb-2">Comment ça marche ?</h2>
          <ul className="text-sm text-gray-700 space-y-2">
            <li>🌱 <b>Créer une partie</b> : Choisis 32 ou 52 cartes. Un ID te sera donné.</li>
            <li>🤝 <b>Partage l’ID</b> avec ton adversaire pour qu’il te rejoigne.</li>
            <li>🎴 <b>Rejoindre une partie</b> : entre simplement l’ID reçu.</li>
            <li>⏳ <b>Parties en attente</b> : rejoins une partie déjà créée.</li>
          </ul>
        </div>

        {/* CRÉATION */}
        <div className="p-5 bg-white rounded-xl border border-green-300 shadow space-y-3">
          <h2 className="text-lg font-semibold text-green-700">Créer une nouvelle partie</h2>

          <div className="flex gap-3">
            <button
              onClick={() => initNewGame(32)}
              className="flex-1 py-2 bg-green-700 text-white rounded-lg shadow hover:bg-green-800"
            >
              🌳 32 cartes
            </button>

            <button
              onClick={() => initNewGame(52)}
              className="flex-1 py-2 bg-green-500 text-white rounded-lg shadow hover:bg-green-600"
            >
              🃏 52 cartes
            </button>
          </div>

          {manualId && (
            <div className="text-xs mt-2 p-2 bg-green-50 border border-green-300 rounded text-green-700">
              Partie créée : <b>{manualId}</b>
              <br />➡ Vous devez la rejoindre maintenant.
            </div>
          )}
        </div>

        {/* REJOINDRE VIA ID */}
        <div className="p-5 bg-white rounded-xl border border-green-300 shadow space-y-3">
          <h2 className="text-lg font-semibold text-green-700">Rejoindre une partie existante</h2>

          <input
            value={manualId}
            onChange={(e) => setManualId(e.target.value)}
            placeholder="ID de la partie"
            className="w-full border rounded-lg px-3 py-2"
          />

          <button
            onClick={() => handleJoin(manualId)}
            disabled={!manualId || loading}
            className="w-full py-2 bg-green-700 text-white rounded-lg shadow hover:bg-green-800 disabled:opacity-40"
          >
            🔗 Rejoindre
          </button>
        </div>

        {/* LISTE DES PARTIES */}
        <div className="p-5 bg-white rounded-xl border border-green-300 shadow space-y-4">
          <h2 className="text-lg font-semibold text-green-700">Parties en attente</h2>

          {waiting.length === 0 ? (
            <p className="text-sm text-gray-600">Aucune partie pour le moment…</p>
          ) : (
            <div className="space-y-3">
              {waiting.map((g) => (
                <div
                  key={g.id}
                  className="border rounded-lg p-4 flex justify-between items-center bg-green-50"
                >
                  <div>
                    <div className="font-bold text-green-700">Partie {g.id}</div>
                    <div className="text-xs text-gray-600">En attente d'un joueur…</div>
                  </div>

                  <button
                    onClick={() => handleJoin(g.id)}
                    disabled={loading}
                    className="px-3 py-1 bg-green-700 text-white rounded shadow hover:bg-green-800"
                  >
                    Rejoindre
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>

      </div>
    </div>
  );
}
