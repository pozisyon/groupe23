import { useEffect, useState } from "react";
import { listOpenGames } from "../api/gameApi";
import { useGame } from "../context/GameContext";

export default function LobbyPage() {
  const [rooms, setRooms] = useState<{ id: string; players: string[]; createdAt: string }[]>([]);
  const { initNewGame, loadGame } = useGame();

  useEffect(() => {
    listOpenGames().then(setRooms).catch(() => setRooms([]));
  }, []);

  return (
    <div className="p-4 space-y-3">
      <div className="flex gap-2">
        <button className="px-3 py-2 rounded bg-blue-600 text-white text-sm" onClick={() => initNewGame(["J1","J2"], "32")}>Nouvelle partie (32)</button>
        <button className="px-3 py-2 rounded bg-indigo-600 text-white text-sm" onClick={() => initNewGame(["J1","J2"], "52")}>Nouvelle partie (52)</button>
      </div>

      <div className="border rounded bg-white">
        <div className="p-2 text-sm font-medium border-b">Parties ouvertes</div>
        <div className="divide-y">
          {rooms.map(r => (
            <div key={r.id} className="p-2 text-sm flex items-center justify-between">
              <div>#{r.id} • {r.players.join(" vs ")}</div>
              <button className="px-2 py-1 rounded border" onClick={() => loadGame(r.id)}>Rejoindre</button>
            </div>
          ))}
          {rooms.length === 0 && <div className="p-2 text-sm text-gray-500">Aucune salle pour le moment.</div>}
        </div>
      </div>
    </div>
  );
}
