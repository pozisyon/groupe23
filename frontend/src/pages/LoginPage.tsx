import React, { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { AuthApi } from "../api/authApi";
import { useAuthStore } from "../store/authStore";

export const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const login = useAuthStore((s) => s.login);

  const token = useAuthStore((s) => s.token);

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);



  useEffect(() => {
    if (token) {
      navigate("/lobby");
    }
  }, [token, navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const res = await AuthApi.login({ email, password });
      login(res.user, res.token);
      navigate("/lobby");
    } catch (err: any) {
      console.error(err);
      setError("Identifiants invalides ou erreur serveur.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card" style={{ maxWidth: 420, margin: "2rem auto" }}>
      <div className="card-header">
        <div>
          <div className="card-title">Connexion</div>
          <div className="card-subtitle">Rejoins le lobby Arbre32</div>
        </div>
      </div>
      <form onSubmit={handleSubmit}>
        <div className="form-row">
          <label>Nom d'utilisateur</label>
          <input
            className="input"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            autoComplete="email"
            required
          />
        </div>
        <div className="form-row">
          <label>Mot de passe</label>
          <input
            className="input"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
            required
          />
        </div>
        {error && (
          <div className="text-center mt-sm" style={{ color: "#f97373", fontSize: "0.85rem" }}>
            {error}
          </div>
        )}
        <div className="form-actions">
          <button className="btn" type="submit" disabled={loading}>
            {loading ? "Connexion..." : "Se connecter"}
          </button>
        </div>
      </form>
      <div className="text-center mt-md">
        <span className="text-muted">Pas encore de compte ? </span>
        <Link to="/register">Créer un compte</Link>
      </div>
    </div>
  );
};