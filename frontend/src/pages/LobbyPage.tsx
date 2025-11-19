import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { GameApi } from "../api/gameApi";
import { useGameStore, GameSummary } from "../store/gameStore";
import { LoadingSpinner } from "../components/common/LoadingSpinner";

export const LobbyPage: React.FC = () => {
  const navigate = useNavigate();
  const games = useGameStore((s) => s.games);
  const setGames = useGameStore((s) => s.setGames);

  const [loading, setLoading] = useState(false);

  const fetchGames = async () => {
    setLoading(true);
    try {
      const list = await GameApi.listGames();
      setGames(list);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchGames();
  }, []);

  const handleCreateGame = async () => {
    try {
      const game = await GameApi.createGame();
      navigate(`/game/${game.id}`);
    } catch (e) {
      console.error(e);
      alert("Erreur lors de la création de la partie.");
    }
  };

  const handleJoin = async (game: GameSummary) => {
    try {
      const joined = await GameApi.joinGame(game.id);
      navigate(`/game/${joined.id}`);
    } catch (e) {
      console.error(e);
      alert("Impossible de rejoindre cette partie.");
    }
  };

  const statusLabel = (status: GameSummary["status"]) => {
    switch (status) {
      case "WAITING":
        return "En attente";
      case "IN_PROGRESS":
        return "En cours";
      case "FINISHED":
        return "Terminée";
    }
  };

  return (
    <div>
      <h1 className="page-title">Lobby des parties</h1>
      <p className="page-description">
        Crée une nouvelle partie d&apos;Arbre32 ou rejoins une partie existante. Tout est synchronisé avec le backend Spring Boot.
      </p>

      <div className="lobby-grid">
        <div className="card">
          <div className="card-header">
            <div>
              <div className="card-title">Parties disponibles</div>
              <div className="card-subtitle">
                {games.length === 0 ? "Aucune partie pour le moment." : `${games.length} partie(s) trouvée(s).`}
              </div>
            </div>
            <button className="btn secondary" onClick={fetchGames} disabled={loading}>
              Rafraîchir
            </button>
          </div>

          {loading && <LoadingSpinner />}

          <div className="lobby-list">
            {games.map((g) => (
              <div key={g.id} className="lobby-item">
                <div className="lobby-item-main">
                  <div className="lobby-item-title">
                    <span className={`status-dot ${g.status.toLowerCase()}`}></span>
                    Partie #{g.id}
                  </div>
                  <div className="lobby-item-meta">
                    <span className={`badge ${g.status.toLowerCase()}`}>{statusLabel(g.status)}</span>{" "}
                    <span style={{ marginLeft: "0.4rem" }}>
                      Joueurs : {g.players && g.players.length > 0 ? g.players.join(", ") : "aucun pour l'instant"}
                    </span>
                  </div>
                </div>
                <div className="lobby-actions">
                  <button
                    className="btn"
                    onClick={() => handleJoin(g)}
                    disabled={g.status === "FINISHED"}
                  >
                    Rejoindre
                  </button>
                  <span className="text-muted">Statut temps réel via WebSocket en jeu</span>
                </div>
              </div>
            ))}
          </div>

          {games.length === 0 && !loading && (
            <div className="text-center mt-md text-muted">
              Crée la première partie pour commencer à jouer.
            </div>
          )}
        </div>

        <div className="card">
          <div className="card-header">
            <div>
              <div className="card-title">Créer une partie</div>
              <div className="card-subtitle">Lance une nouvelle session Arbre32</div>
            </div>
          </div>
          <p className="text-muted">
            Une nouvelle partie sera créée côté backend, et tu seras automatiquement redirigé vers le plateau.
          </p>
          <button className="btn mt-md" onClick={handleCreateGame}>
            Créer une nouvelle partie
          </button>
          <p className="mt-md text-muted">
            Tous les états de jeu (cartes, tours, scores) seront gérés par le backend Spring Boot et diffusés en temps réel via WebSocket.
          </p>
        </div>
      </div>
    </div>
  );
};