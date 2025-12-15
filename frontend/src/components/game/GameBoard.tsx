// src/components/game/GameBoard.tsx
import { useGame } from "../../context/GameContext";
import { useAuthStore } from "../../store/authStore";
import Card from "./Card";

export default function GameBoard() {
  const { game, play, lastError } = useGame();
  const { user } = useAuthStore();

  if (!game) {
    return <div className="p-4 opacity-70">Aucune partie chargée.</div>;
  }

  const myHandle = (user as any)?.handle;
  const isMyTurn = !!myHandle && game.currentPlayer === myHandle;

  return (
    <div className="border dark:border-[#3a3b40] rounded-2xl bg-white dark:bg-[#2a2c31] p-3">

      {/* 🟦 BANNIÈRE BEST OF 3 */}
      <div className="mb-4 p-3 rounded-lg bg-blue-900 text-white text-center">
        <h2 className="text-lg font-semibold">Manche {game.round} / 3</h2>

        <div className="flex justify-center gap-6 mt-2">
          {game.players.map((p) => (
            <div key={p.id} className="text-center">
              <div className="font-semibold">{p.name}</div>
              <div className="text-xl">
                🏆 {game.roundsWon?.[p.id] ?? 0}
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* 🟥 FIN DU MATCH (Best-of-3 terminé) */}
      {game.matchOver && (
        <div className="fixed inset-0 flex items-center justify-center bg-black/60 backdrop-blur-sm z-50">
          <div className="bg-white dark:bg-[#2a2c31] p-6 rounded-xl shadow-xl text-center max-w-sm mx-auto">
            <h1 className="text-2xl font-bold mb-4">🏁 Match terminé !</h1>
            <p className="text-lg mb-6">
              Vainqueur : <span className="font-semibold">{game.matchWinner}</span>
            </p>
          </div>
        </div>
      )}

      {/* INFO TOUR */}
      <div className="mb-2 flex flex-col gap-1">
        <div className="text-sm">
          Joueur courant :{" "}
          <span className="font-semibold">{game.currentPlayer ?? "—"}</span>
        </div>

        {myHandle && (
          <div
            className={`text-xs px-2 py-1 rounded ${
              isMyTurn
                ? "bg-green-100 text-green-800"
                : "bg-yellow-100 text-yellow-800"
            }`}
          >
            {isMyTurn ? "C'est votre tour, vous pouvez jouer." : "Ce n'est pas votre tour."}
          </div>
        )}

        {lastError && (
          <div className="text-xs px-2 py-1 rounded bg-red-100 text-red-800">
            {lastError}
          </div>
        )}
      </div>

      {/* PLATEAU */}
      <div className="text-sm font-medium mb-2">Plateau</div>
      <div className="grid grid-rows-4 grid-cols-8 gap-2">
        {game.board.map((row, rIdx) =>
          row.map((card, cIdx) => (
            <div className="h-24" key={`${rIdx}-${cIdx}`}>
              <Card
                card={card}
                onClick={() => {
                  if (!isMyTurn || game.matchOver) return;
                  play(card.id);
                }}
              />
            </div>
          ))
        )}
      </div>
    </div>
  );
}
