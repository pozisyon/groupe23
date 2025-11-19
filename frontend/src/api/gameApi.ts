import api from "./http";
import { GameSummary, GameState } from "../store/gameStore";

export const GameApi = {
  async listGames(): Promise<GameSummary[]> {
    const res = await api.get<GameSummary[]>("/api/games");
    return res.data;
  },

  async createGame(): Promise<GameState> {
    const res = await api.post<GameState>("/api/games");
    return res.data;
  },

  async joinGame(id: string): Promise<GameState> {
    const res = await api.post<GameState>(`/api/games/${id}/join`);
    return res.data;
  },

  async getGame(id: string): Promise<GameState> {
    const res = await api.get<GameState>(`/api/games/${id}`);
    return res.data;
  },

  async playMove(id: string, payload: unknown): Promise<void> {
    await api.post(`/api/games/${id}/move`, payload);
  },
};