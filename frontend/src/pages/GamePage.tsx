import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { GameApi } from "../api/gameApi";
import { useGameStore, GameState } from "../store/gameStore";
import { gameSocket } from "../websocket/socket";
import { LoadingSpinner } from "../components/common/LoadingSpinner";
import { useAuthStore } from "../store/authStore";

interface ChatMessage {
  author: string;
  content: string;
  timestamp: string;
}

export const GamePage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const currentGame = useGameStore((s) => s.currentGame);
  const setCurrentGame = useGameStore((s) => s.setCurrentGame);
  const user = useAuthStore((s) => s.user);

  const [loading, setLoading] = useState(false);
  const [chatMessages, setChatMessages] = useState<ChatMessage[]>([]);
  const [chatInput, setChatInput] = useState("");

  useEffect(() => {
    if (!id) return;

    const loadGame = async () => {
      setLoading(true);
      try {
        const game = await GameApi.getGame(id);
        setCurrentGame(game);
      } catch (e) {
        console.error(e);
      } finally {
        setLoading(false);
      }
    };

    loadGame();
  }, [id, setCurrentGame]);

  useEffect(() => {
    if (!id) return;

    gameSocket.subscribe(`/topic/game/${id}`, (payload: GameState) => {
      setCurrentGame(payload);
    });

    gameSocket.subscribe(`/topic/chat/${id}`, (payload: ChatMessage) => {
      setChatMessages((prev) => [...prev, payload]);
    });
  }, [id, setCurrentGame]);

  const handleSendChat = () => {
    if (!id || !chatInput.trim()) return;
    const message: ChatMessage = {
      author: user?.username ?? "Anonyme",
      content: chatInput.trim(),
      timestamp: new Date().toISOString(),
    };
    setChatInput("");
    gameSocket.send(`/app/chat/${id}`, message);
  };

  const handlePlayDemoMove = () => {
    if (!id) return;
    const payload = { type: "DEMO_MOVE", playedAt: new Date().toISOString() };
    gameSocket.send(`/app/game/${id}/move`, payload);
  };

  if (!id) {
    return <div>Id de partie manquant.</div>;
  }

  return (
    <div>
      <h1 className="page-title">Partie #{id}</h1>
      <p className="page-description">
        Plateau de jeu Arbre32 connecté au backend. Les états sont poussés en temps réel via WebSocket.
      </p>

      <div className="game-layout">
        <div className="card game-board">
          <div className="card-header">
            <div>
              <div className="card-title">Plateau de jeu</div>
              <div className="card-subtitle">
                Vue simplifiée : intègre ici ton rendu du plateau, des cartes et de l&apos;arbre.
              </div>
            </div>
            <div className="pill">
              <span className="pill-dot" />
              Temps réel activé
            </div>
          </div>

          {loading && <LoadingSpinner />}

          {currentGame ? (
            <>
              <div className="mt-md">
                <div className="game-section-title">Informations de la partie</div>
                <p className="text-muted">
                  Statut : <strong>{currentGame.status}</strong>{" "}
                  {currentGame.currentPlayer && (
                    <>
                      • Joueur courant : <strong>{currentGame.currentPlayer}</strong>
                    </>
                  )}
                </p>
                <p className="text-muted">
                  Joueurs :{" "}
                  {currentGame.players && currentGame.players.length > 0
                    ? currentGame.players.join(", ")
                    : "aucun joueur listé"}
                </p>
              </div>

              <div className="mt-md">
                <div className="game-section-title">Actions de test</div>
                <p className="text-muted">
                  Ce bouton envoie une action factice au backend (`/app/game/{id}/move`). Remplace cette logique par
                  tes véritables coups de jeu Arbre32.
                </p>
                <button className="btn mt-sm" onClick={handlePlayDemoMove}>
                  Envoyer un coup de test
                </button>
              </div>
            </>
          ) : (
            !loading && <p className="text-muted mt-md">Aucune donnée de partie pour le moment.</p>
          )}
        </div>

        <div className="card">
          <div className="card-header">
            <div>
              <div className="card-title">Chat de la partie</div>
              <div className="card-subtitle">Discute avec les autres joueurs en temps réel.</div>
            </div>
          </div>
          <div className="chat-container">
            <div className="chat-messages">
              {chatMessages.length === 0 && (
                <div className="text-muted">Aucun message pour le moment. Dis bonjour !</div>
              )}
              {chatMessages.map((m, index) => (
                <div key={index} className="chat-message">
                  <span className="author">{m.author} :</span>
                  <span>{m.content}</span>
                </div>
              ))}
            </div>
            <div className="chat-input-row">
              <input
                className="input"
                placeholder="Écrire un message..."
                value={chatInput}
                onChange={(e) => setChatInput(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === "Enter") {
                    handleSendChat();
                  }
                }}
              />
              <button className="btn" onClick={handleSendChat}>
                Envoyer
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};