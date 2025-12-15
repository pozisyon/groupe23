import { Separator } from "@/components/ui/separator";

export default function AdminLayout({ children }) {
  return (
    <div className="h-screen flex">
      {/* SIDEBAR */}
      <div className="w-64 bg-[#1e1f24] text-white p-5 space-y-4">
        <h1 className="text-xl font-bold">Arbre32 Admin</h1>
        <Separator className="bg-white/20" />

        <nav className="space-y-2">
          <a className="block hover:text-blue-400" href="/admin">📊 Dashboard</a>
          <a className="block hover:text-blue-400" href="/admin/games">🎮 Parties</a>
          <a className="block hover:text-blue-400" href="/admin/live">📡 Live Monitor</a>
        </nav>
      </div>

      {/* CONTENU */}
      <div className="flex-1 bg-gray-100 p-6 overflow-auto">{children}</div>
    </div>
  );
}
