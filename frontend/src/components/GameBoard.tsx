import { useGame } from "../context/GameContext";
import Card from "./Card";

export default function GameBoard() {
  const { game, play } = useGame();
  if (!game) return <div className="p-4 text-gray-500">Aucune partie chargée.</div>;

  return (
    <div className="border rounded-2xl bg-white p-3">
      <div className="text-sm font-medium mb-2">Plateau (4×8)</div>
      <div className="grid grid-rows-4 grid-cols-8 gap-2">
        {game.board.map((row, rIdx) =>
          row.map((card, cIdx) => (
            <div className="h-24" key={`${rIdx}-${cIdx}`}>
              <Card card={card} onClick={(id) => play(id)} />
            </div>
          ))
        )}
      </div>
    </div>
  );
}
