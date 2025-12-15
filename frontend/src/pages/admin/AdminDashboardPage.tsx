// src/pages/admin/AdminDashboardPage.tsx
import { useEffect, useState } from "react";
import { AdminApi, AdminGameSummary, AdminDashboardStats } from "../../api/AdminApi";
import { Link } from "react-router-dom";
import AdminStatsGrid from "../../components/admin/AdminStatsGrid";
import GameStatusBadge from "../../components/admin/GameStatusBadge";

export default function AdminDashboardPage() {
  const [stats, setStats] = useState<AdminDashboardStats | null>(null);
  const [games, setGames] = useState<AdminGameSummary[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    (async () => {
      try {
        const [st, gs] = await Promise.all([
          AdminApi.getDashboardStats().catch(() => null),
          AdminApi.listGames(),
        ]);

        setStats(
          st ?? {
            totalGames: gs.length,
            activeGames: gs.filter((g) => g.status === "IN_PROGRESS").length,
            finishedGames: gs.filter((g) => g.status === "FINISHED").length,
            avgRoundsPerMatch: 3,
          }
        );
        setGames(gs);
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  return (
    <div className="min-h-screen bg-slate-100 dark:bg-[#111827] text-slate-900 dark:text-slate-100">
      <div className="max-w-6xl mx-auto py-6 px-4">
        <header className="flex items-center justify-between mb-6">
          <div>
            <h1 className="text-2xl font-bold tracking-tight">
              Arbre32 – Dashboard Admin
            </h1>
            <p className="text-sm text-slate-500 dark:text-slate-400">
              Supervision des matchs, joueurs et best-of-3 en temps réel.
            </p>
          </div>

          <div className="flex gap-2">
            <Link
              to="/admin/games/new"
              className="px-3 py-2 rounded-xl text-sm font-medium bg-blue-600 text-white hover:bg-blue-700"
            >
              + Nouvelle Partie
            </Link>
          </div>
        </header>

        {stats && <AdminStatsGrid stats={stats} />}

        <section className="mt-6">
          <div className="flex items-center justify-between mb-3">
            <h2 className="text-lg font-semibold">Parties en cours & récentes</h2>
          </div>

          {loading ? (
            <div className="text-sm opacity-70">Chargement des parties…</div>
          ) : games.length === 0 ? (
            <div className="text-sm opacity-70">
              Aucune partie trouvée pour le moment.
            </div>
          ) : (
            <div className="overflow-hidden rounded-2xl bg-white dark:bg-[#020617] shadow-md border border-slate-200/70 dark:border-slate-800">
              <table className="min-w-full text-sm">
                <thead className="bg-slate-50 dark:bg-slate-900/60">
                  <tr>
                    <th className="px-4 py-3 text-left font-medium">Partie</th>
                    <th className="px-4 py-3 text-left font-medium">Joueurs</th>
                    <th className="px-4 py-3 text-left font-medium">Statut</th>
                    <th className="px-4 py-3 text-left font-medium">Round</th>
                    <th className="px-4 py-3 text-left font-medium">Scores</th>
                    <th className="px-4 py-3 text-right font-medium">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {games.map((g) => (
                    <tr
                      key={g.gameId}
                      className="border-t border-slate-100 dark:border-slate-800/80 hover:bg-slate-50/70 dark:hover:bg-slate-900/60"
                    >
                      <td className="px-4 py-3 align-middle">
                        <div className="font-mono text-xs">
                          #{g.gameId.slice(0, 8)}
                        </div>
                        <div className="text-[11px] text-slate-500 dark:text-slate-400">
                          {new Date(g.createdAt).toLocaleTimeString("fr-CA", {
                            hour: "2-digit",
                            minute: "2-digit",
                          })}
                        </div>
                      </td>
                      <td className="px-4 py-3 align-middle">
                        <div className="flex flex-col gap-0.5">
                          {g.players.map((p) => (
                            <span key={p.id} className="text-xs">
                              {p.name}
                            </span>
                          ))}
                        </div>
                      </td>
                      <td className="px-4 py-3 align-middle">
                        <GameStatusBadge status={g.status} />
                      </td>
                      <td className="px-4 py-3 align-middle text-xs">
                        {g.currentRound}/{g.totalRounds}
                      </td>
                      <td className="px-4 py-3 align-middle text-xs">
                        {g.players.map((p) => (
                          <div key={p.id}>
                            {p.name}: <span className="font-semibold">{p.score}</span>{" "}
                            pts – {p.roundsWon} manche(s)
                          </div>
                        ))}
                      </td>
                      <td className="px-4 py-3 align-middle text-right">
                        <Link
                          to={`/admin/games/${g.gameId}`}
                          className="text-xs px-3 py-1.5 rounded-lg bg-slate-900 text-slate-50 dark:bg-slate-100 dark:text-slate-900 hover:opacity-90"
                        >
                          Voir / Spectateur
                        </Link>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </div>
    </div>
  );
}
