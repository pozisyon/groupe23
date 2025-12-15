// src/components/admin/GameStatusBadge.tsx
import type { GameStatus } from "../../api/AdminApi";

export default function GameStatusBadge({ status }: { status: GameStatus }) {
  let label = "";
  let classes = "";

  switch (status) {
    case "WAITING":
      label = "En attente";
      classes = "bg-amber-100 text-amber-800 border border-amber-200";
      break;
    case "IN_PROGRESS":
      label = "En cours";
      classes = "bg-emerald-100 text-emerald-800 border border-emerald-200";
      break;
    case "FINISHED":
      label = "Terminé";
      classes = "bg-slate-200 text-slate-800 border border-slate-300";
      break;
  }

  return (
    <span
      className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-[11px] font-medium ${classes}`}
    >
      {label}
    </span>
  );
}
