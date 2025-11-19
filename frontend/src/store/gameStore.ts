import { create } from "zustand";

export type GameStatus = "WAITING" | "IN_PROGRESS" | "FINISHED";

export interface GameSummary {
  id: string;
  status: GameStatus;
  players: string[];
}

export interface GameState {
  id: string;
  status: GameStatus;
  currentPlayer?: string;
  players: string[];
  // Ajoute ici d'autres propriétés selon ton modèle backend
}

interface InternalState {
  games: GameSummary[];
  currentGame: GameState | null;
  setGames: (games: GameSummary[]) => void;
  setCurrentGame: (game: GameState | null) => void;
}

export const useGameStore = create<InternalState>((set) => ({
  games: [],
  currentGame: null,
  setGames: (games) => set({ games }),
  setCurrentGame: (currentGame) => set({ currentGame }),
}));