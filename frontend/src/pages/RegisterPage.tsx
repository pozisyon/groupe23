import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { AuthApi } from "../api/authApi";

export const RegisterPage: React.FC = () => {
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [confirm, setConfirm] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    if (password !== confirm) {
      setError("Les mots de passe ne correspondent pas.");
      return;
    }

    setLoading(true);
    try {
      await AuthApi.register({ username, password });
      setSuccess("Compte créé avec succès. Tu peux maintenant te connecter.");
      setTimeout(() => navigate("/login"), 1200);
    } catch (err: any) {
      console.error(err);
      setError("Erreur lors de la création du compte.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card" style={{ maxWidth: 420, margin: "2rem auto" }}>
      <div className="card-header">
        <div>
          <div className="card-title">Créer un compte</div>
          <div className="card-subtitle">Rejoins les parties Arbre32 en ligne</div>
        </div>
      </div>
      <form onSubmit={handleSubmit}>
        <div className="form-row">
          <label>Nom d'utilisateur</label>
          <input
            className="input"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
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
            required
          />
        </div>
        <div className="form-row">
          <label>Confirmer le mot de passe</label>
          <input
            className="input"
            type="password"
            value={confirm}
            onChange={(e) => setConfirm(e.target.value)}
            required
          />
        </div>
        {error && (
          <div className="text-center mt-sm" style={{ color: "#f97373", fontSize: "0.85rem" }}>
            {error}
          </div>
        )}
        {success && (
          <div className="text-center mt-sm" style={{ color: "#4ade80", fontSize: "0.85rem" }}>
            {success}
          </div>
        )}
        <div className="form-actions">
          <button className="btn" type="submit" disabled={loading}>
            {loading ? "Création..." : "Créer le compte"}
          </button>
        </div>
      </form>
      <div className="text-center mt-md">
        <span className="text-muted">Déjà inscrit ? </span>
        <Link to="/login">Se connecter</Link>
      </div>
    </div>
  );
};