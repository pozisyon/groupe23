import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { AdminApi, AdminGame } from "../../api/AdminApi";

export default function AdminGameDetail() {
  const { id } = useParams();
  const [game, setGame] = useState<AdminGame | null>(null);

  async function load() {
    if (!id) return;
    const g = await AdminApi.get(id);
    setGame(g);
  }

  useEffect(() => {
    load();
  }, [id]);

  if (!game) return <div>Chargement...</div>;

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold">Partie {id}</h1>

      <div className="mt-4 space-y-2">
        <div>État : {game.gameOver ? "Terminé" : "En cours"}</div>
        <div>Round actuel : {game.round}</div>

        <div>
          Manches gagnées :
          <ul className="list-disc ml-6">
            {Object.entries(game.roundsWon).map(([p, w]) => (
              <li key={p}>
                {p} → {w}
              </li>
            ))}
          </ul>
        </div>
      </div>

      <div className="mt-6 space-x-3">
        <button
          onClick={() => AdminApi.forceFinish(id!).then(load)}
          className="px-3 py-2 bg-red-600 text-white rounded"
        >
          🔥 Forcer fin du match
        </button>

        <button
          onClick={() => AdminApi.nextRound(id!).then(load)}
          className="px-3 py-2 bg-blue-600 text-white rounded"
        >
          ➡️ Passer au round suivant
        </button>

        <button
          onClick={() => AdminApi.reset(id!).then(load)}
          className="px-3 py-2 bg-gray-700 text-white rounded"
        >
          ♻️ Reset complet du match
        </button>
      </div>
    </div>
  );
}
