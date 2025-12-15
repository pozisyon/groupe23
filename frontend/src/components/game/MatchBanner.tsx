import React from "react";

interface Props {
  round: number;
  players: { id: string; name: string; score: number }[];
  roundsWon: Record<string, number>;
  winner?: string | null;
  gameOver: boolean;
}

export const MatchBanner: React.FC<Props> = ({
  round,
  players,
  roundsWon,
  winner,
  gameOver,
}) => {
  if (gameOver && winner) {
    return (
      <div className="w-full bg-yellow-400 text-black font-bold py-3 text-center text-xl rounded-md shadow-lg">
        🏆 Match terminé — Gagnant : {winner}
      </div>
    );
  }

  return (
    <div className="w-full bg-blue-600 text-white py-2 px-4 rounded-md shadow text-center">
      <div className="text-lg font-semibold">
        🕹️ Manche <span className="font-bold">{round}</span> / 3
      </div>

      <div className="flex justify-center gap-6 mt-2">
        {players.map((p) => (
          <div key={p.id} className="text-sm">
            <span className="font-bold">{p.name}</span> :
            <span className="ml-1 text-yellow-300 font-bold">
              {roundsWon[p.id] ?? 0}
            </span>
            <span className="text-xs"> manche(s)</span>
          </div>
        ))}
      </div>
    </div>
  );
};
