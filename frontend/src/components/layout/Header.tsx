/*import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { useAuthStore } from '../../store/authStore'

export default function Header({ dark, onToggleDark }: { dark: boolean; onToggleDark: ()=>void }) {
  const user = useAuthStore(s => s.user)
  return (
    <header className="w-full h-16 bg-white dark:bg-[#2a2c31] border-b border-gray-200/80 dark:border-[#3a3b40] flex items-center px-4 gap-6">
      <motion.div initial={{ scale: 0.8, opacity: 0 }} animate={{ scale: 1, opacity: 1 }} className="w-8 h-8 rounded-lg bg-brand-600" />
      <h1 className="font-semibold">Arbre32</h1>
      <nav className="ml-6 flex gap-4 text-sm">
        <Link to="/">Accueil</Link>
        <Link to="/lobby">Lobby</Link>
        <Link to="/game">Partie</Link>
      </nav>
      <div className="ml-auto flex items-center gap-3 text-sm">
        {user && <span className="opacity-80">Connecté : <b>{user.email}</b></span>}
        <button onClick={onToggleDark} className="text-sm px-2 py-1 rounded border dark:border-[#3a3b40]">
          {dark ? 'Thème • Sombre' : 'Thème • Clair'}
        </button>
      </div>
    </header>
  )
}*/
import { Link, useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import { useAuthStore } from "../../store/authStore";

export default function Header({
  dark,
  onToggleDark,
}: {
  dark: boolean;
  onToggleDark: () => void;
}) {
  const { user, logout } = useAuthStore();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <header className="w-full h-16 bg-white dark:bg-[#2a2c31] border-b border-gray-200/80 dark:border-[#3a3b40] flex items-center px-4 gap-6">

      {/* LOGO */}
      <motion.div
        initial={{ scale: 0.8, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        className="w-8 h-8 rounded-lg bg-brand-600"
      />

      <h1 className="font-semibold">Arbre32</h1>

      {/* NAVIGATION */}
      <nav className="ml-6 flex gap-4 text-sm">
        <Link to="/">Accueil</Link>
        <Link to="/lobby">Lobby</Link>
        <Link to="/game">Partie</Link>
      </nav>

      {/* DROITE */}
      <div className="ml-auto flex items-center gap-3 text-sm">

        {/* SI CONNECTÉ → afficher identifiant */}
        {user && (
          <span className="opacity-80">
            Connecté : <b>{user.handle ?? user.email}</b>
          </span>
        )}

        {/* SI CONNECTÉ → bouton déconnexion */}
        {user ? (
          <button
            onClick={handleLogout}
            className="px-3 py-1 rounded bg-red-500 text-white hover:bg-red-600 transition"
          >
            Déconnexion
          </button>
        ) : (
          <>
            {/* SI NON CONNECTÉ → afficher les boutons pour login + thème */}
            <Link
              to="/login"
              className="px-3 py-1 rounded bg-brand-600 text-white hover:bg-brand-700"
            >
              Connexion
            </Link>
            <Link
              to="/register"
              className="px-3 py-1 rounded border border-gray-300 dark:border-[#3a3b40]"
            >
              Créer un compte
            </Link>

            {/* BOUTON THEME SI NON CONNECTÉ */}
            <button
              onClick={onToggleDark}
              className="text-sm px-2 py-1 rounded border dark:border-[#3a3b40]"
            >
              {dark ? "Sombre" : "Clair"}
            </button>
          </>
        )}
      </div>
    </header>
  );
}

