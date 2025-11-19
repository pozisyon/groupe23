import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuthStore } from "../../store/authStore";

export const Header: React.FC = () => {
  const navigate = useNavigate();
  const user = useAuthStore((s) => s.user);
  const logout = useAuthStore((s) => s.logout);

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <header className="app-header">
      <div className="app-title">
        <span className="logo">32</span>
        Arbre32 • Jeu de stratégie
      </div>
      <div className="app-header-right">
        {user && <div className="app-user">Connecté : <strong>{user.username}</strong></div>}
        {user ? (
          <button className="btn secondary" onClick={handleLogout}>
            Déconnexion
          </button>
        ) : (
          <>
            <Link to="/login">
              <button className="btn secondary">Connexion</button>
            </Link>
            <Link to="/register">
              <button className="btn">Créer un compte</button>
            </Link>
          </>
        )}
      </div>
    </header>
  );
};