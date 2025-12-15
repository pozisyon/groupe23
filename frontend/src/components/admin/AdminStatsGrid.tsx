// src/components/admin/AdminStatsGrid.tsx
import type { AdminDashboardStats } from "../../api/AdminApi";

interface Props {
  stats: AdminDashboardStats;
}

export default function AdminStatsGrid({ stats }: Props) {
  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      <StatCard
        label="Total des parties"
        value={stats.totalGames}
        subtitle="Depuis le dernier redémarrage"
      />
      <StatCard
        label="Parties actives"
        value={stats.activeGames}
        subtitle="En cours de jeu"
      />
      <StatCard
        label="Parties terminées"
        value={stats.finishedGames}
        subtitle="Best-of-3 ou abandons"
      />
      <StatCard
        label="Rounds moyens / match"
        value={stats.avgRoundsPerMatch.toFixed(1)}
        subtitle="Best-of-3"
      />
    </div>
  );
}

function StatCard({
  label,
  value,
  subtitle,
}: {
  label: string;
  value: string | number;
  subtitle?: string;
}) {
  return (
    <div className="rounded-2xl bg-white dark:bg-[#020617] border border-slate-200/80 dark:border-slate-800 p-4 shadow-sm">
      <div className="text-[11px] uppercase tracking-wide text-slate-500 dark:text-slate-400 mb-1">
        {label}
      </div>
      <div className="text-2xl font-semibold mb-1">{value}</div>
      {subtitle && (
        <div className="text-[11px] text-slate-500 dark:text-slate-500">
          {subtitle}
        </div>
      )}
    </div>
  );
}
