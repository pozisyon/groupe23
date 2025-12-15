import { useEffect, useState } from "react";
import { AdminApi } from "../../api/AdminApi";

export default function AdminDashboard() {
  const [games, setGames] = useState([]);

  useEffect(() => {
    AdminApi.getGames().then(setGames);
  }, []);

  return (
    <div className="p-6">
      <h1 className="text-lg font-bold mb-4">Dashboard Admin</h1>

      <button
        className="bg-green-600 text-white px-4 py-2 rounded mb-4"
        onClick={() => AdminApi.createGame().then(() => window.location.reload())}
      >
        Créer une partie
      </button>

      <table className="w-full border text-sm">
        <thead>
          <tr className="bg-gray-100">
            <th>ID</th>
            <th>Round</th>
            <th>Joueurs</th>
            <th>État</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {games.map((g: any) => (
            <tr key={g.id} className="border-b">
              <td className="p-2">{g.id}</td>
              <td>{g.round}</td>
              <td>{g.players.join(", ")}</td>
              <td>{g.gameOver ? "Terminé" : "En cours"}</td>
              <td className="flex gap-2 p-2">
                <button
                  className="text-blue-600"
                  onClick={() => (window.location.href = `/admin/game/${g.id}`)}
                >
                  Voir
                </button>

                <button
                  className="text-orange-600"
                  onClick={() => AdminApi.resetGame(g.id)}
                >
                  Reset
                </button>

                <button
                  className="text-red-600"
                  onClick={() => AdminApi.deleteGame(g.id)}
                >
                  Supprimer
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
