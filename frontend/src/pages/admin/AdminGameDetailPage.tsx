// src/pages/admin/AdminGameDetailPage.tsx
import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { AdminApi, AdminGameDetail } from "../../api/AdminApi";
import GameStatusBadge from "../../components/admin/GameStatusBadge";
import GameBoard from "../../components/game/GameBoard"; // en lecture seule
import { useGame } from "../../context/GameContext";

export default function AdminGameDetailPage() {
  const { id } = useParams();
  const [game, setGame] = useState<AdminGameDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [forceEnding, setForceEnding] = useState(false);
  const { setExternalGameState } = useGame() as any; // si tu exposes ça

  useEffect(() => {
    if (!id) return;
    (async () => {
      try {
        const dto = await AdminApi.getGame(id);
        setGame(dto);
        // Option "spectateur" : pousser l'état dans ton GameContext si tu veux réutiliser GameBoard
        if (setExternalGameState) {
          setExternalGameState(dto);
        }
      } finally {
        setLoading(false);
      }
    })();
  }, [id, setExternalGameState]);

  async function handleForceEnd() {
    if (!id) return;
    setForceEnding(true);
    try {
      await AdminApi.forceEndGame(id);
      const dto = await AdminApi.getGame(id);
      setGame(dto);
      if (setExternalGameState) setExternalGameState(dto);
    } finally {
      setForceEnding(false);
    }
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-100 dark:bg-[#020617] p-6">
        <div className="max-w-5xl mx-auto text-sm opacity-70">
          Chargement de la partie #{id}…
        </div>
      </div>
    );
  }

  if (!game) {
    return (
      <div className="min-h-screen bg-slate-100 dark:bg-[#020617] p-6">
        <div className="max-w-5xl mx-auto text-sm">
          Partie introuvable.{" "}
          <Link to="/admin" className="text-blue-600 underline">
            Retour au dashboard
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-100 dark:bg-[#020617] text-slate-900 dark:text-slate-100">
      <div className="max-w-5xl mx-auto py-6 px-4 space-y-4">
        <div className="flex items-center justify-between">
          <div>
            <div className="text-xs text-slate-500 mb-1">
              <Link to="/admin" className="underline">
                Dashboard admin
              </Link>{" "}
              / Partie #{game.gameId.slice(0, 8)}
            </div>
            <h1 className="text-xl font-semibold flex items-center gap-2">
              Partie #{game.gameId.slice(0, 8)}
              <GameStatusBadge status={game.status} />
            </h1>
            <div className="text-xs text-slate-500">
              Round {game.currentRound}/{game.maxDepth /* ou totalRounds si tu l'ajoutes */}
            </div>
          </div>

          <div className="flex gap-2">
            <button
              onClick={handleForceEnd}
              disabled={forceEnding || game.status === "FINISHED"}
              className="px-3 py-2 rounded-xl text-xs font-medium border border-red-500 text-red-600 hover:bg-red-50 disabled:opacity-60"
            >
              Forcer fin de partie
            </button>
          </div>
        </div>

        <section className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="md:col-span-2">
            <div className="text-sm font-medium mb-1">Plateau (vue spectateur)</div>
            {/* Ici tu peux utiliser GameBoard modifié en mode read-only si besoin */}
            <GameBoard />
          </div>

          <div className="space-y-3">
            <div className="rounded-2xl bg-white dark:bg-slate-900 border border-slate-200/70 dark:border-slate-800 p-3">
              <div className="text-xs font-semibold mb-2">Joueurs & scores</div>
              <div className="space-y-1 text-xs">
                {game.players.map((p) => (
                  <div key={p.id} className="flex justify-between">
                    <span>{p.name}</span>
                    <span className="font-semibold">{p.score} pts</span>
                  </div>
                ))}
              </div>
            </div>

            {game.currentPlayer && (
              <div className="rounded-2xl bg-indigo-50 dark:bg-indigo-950/40 border border-indigo-200/70 dark:border-indigo-900 p-3 text-xs">
                <div className="font-semibold mb-1">Joueur courant</div>
                <div>{game.currentPlayer}</div>
              </div>
            )}
          </div>
        </section>
      </div>
    </div>
  );
}
