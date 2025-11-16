import { useGame } from "../context/GameContext";

export default function GameInfoBar() {
  const { game } = useGame();
  if (!game) return null;
  return (
    <div className="border rounded-lg bg-white px-3 py-2 text-sm flex justify-between">
      <div>
        Partie <b>#{game.gameId}</b> • Joueur en cours : <b>{game.turnPlayer}</b> • Profondeur autorisée : <b>{game.maxDepthAllowed}</b>
      </div>
      <div className="text-gray-500">Racine verrouillée : {game.rootLocked ? "oui" : "non"}</div>
    </div>
  );
}
