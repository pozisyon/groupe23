export default function Footer() {
  return (
    <footer className="w-full mt-10 py-6 border-t border-gray-300/60 dark:border-[#3a3b40] bg-white dark:bg-[#2a2c31]">
      <div className="max-w-4xl mx-auto px-4 text-center text-sm opacity-80 space-y-1">

        <div className="font-semibold text-brand-600">
          Projet Arbre32 — UQTR
        </div>

        <div>
          <span className="font-medium">Cours :</span> INF1014 — Génie logiciel
        </div>

        <div>
          <span className="font-medium">Professeur :</span> William Flageol
        </div>

        <div>
          <span className="font-medium">Étudiants :</span> Gnagna • Faye • Trompe
        </div>

        <div className="text-xs opacity-60 mt-2">
          © {new Date().getFullYear()} — Travail académique, Université du Québec à Trois-Rivières
        </div>

      </div>
    </footer>
  );
}
