import { useParams } from "react-router-dom";
import { useAdminLive } from "../../hooks/useAdminLive";
import GameBoard from "../../components/game/GameBoard";

export default function AdminSpectate() {
  const { id } = useParams();
  const game = useAdminLive(id!);

  if (!game) return <div>Chargement live…</div>;

  return (
    <div>
      <h1 className="text-2xl font-bold mb-4">🎥 Spectateur — Partie {id}</h1>

      <div className="mb-4">
        Joueur courant : {game.currentPlayer}
      </div>

      <GameBoard adminMode game={game} />
    </div>
  );
}
