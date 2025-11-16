import React, { createContext, useContext, useEffect, useState, useCallback } from "react";
import { GameStateDTO, PlayRequestDTO } from "../dto/game";
import { createGame, getGameState, playCard } from "../api/gameApi";
import { Client, IMessage } from "@stomp/stompjs";
import { createStompClient, subscribeGameUpdates } from "../api/ws";

type GameCtx = {
  game?: GameStateDTO;
  loading: boolean;
  initNewGame: (players?: string[], mode?: "32" | "52") => Promise<void>;
  loadGame: (id: string) => Promise<void>;
  play: (cardId: string) => Promise<void>;
};

const Ctx = createContext<GameCtx | null>(null);
export const useGame = () => {
  const ctx = useContext(Ctx);
  if (!ctx) throw new Error("useGame must be used within GameProvider");
  return ctx;
};

export const GameProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [game, setGame] = useState<GameStateDTO>();
  const [loading, setLoading] = useState(false);
  const [client, setClient] = useState<Client | null>(null);
  const [subHandle, setSubHandle] = useState<{ unsubscribe: () => void } | null>(null);

  const connectWS = useCallback((gameId: string) => {
    const c = createStompClient(() => {
      const h = subscribeGameUpdates(c, gameId, (msg: IMessage) => {
        try {
          const state: GameStateDTO = JSON.parse(msg.body);
          setGame(state);
        } catch {}
      });
      setSubHandle(h);
    });
    c.activate();
    setClient(c);
  }, []);

  const disconnectWS = useCallback(() => {
    if (subHandle) subHandle.unsubscribe();
    if (client?.active) client.deactivate();
  }, [client, subHandle]);

  useEffect(() => () => { disconnectWS(); }, [disconnectWS]);

  const initNewGame = async (players = ["J1","J2"], mode: "32" | "52" = "32") => {
    setLoading(true);
    try {
      const { gameId } = await createGame(players, mode);
      const state = await getGameState(gameId);
      setGame(state);
      connectWS(gameId);
    } finally {
      setLoading(false);
    }
  };

  const loadGame = async (id: string) => {
    setLoading(true);
    try {
      const state = await getGameState(id);
      setGame(state);
      connectWS(id);
    } finally {
      setLoading(false);
    }
  };

  const play = async (cardId: string) => {
    if (!game) return;
    const payload: PlayRequestDTO = { cardId };
    const newState = await playCard(game.gameId, payload);
    setGame(newState);
    // Le serveur peut aussi re-diffuser via WS pour synchroniser d'autres clients.
  };

  return (
    <Ctx.Provider value={{ game, loading, initNewGame, loadGame, play }}>
      {children}
    </Ctx.Provider>
  );
};
