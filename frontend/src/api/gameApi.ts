import { api } from "./apiClient";
import { GameStateDTO, PlayRequestDTO } from "../dto/game";

export async function createGame(players: string[] = ["P1", "P2"], mode: "32" | "52" = "32") {
  const res = await api.post<{ gameId: string }>("/api/game/create", { players, mode });
  return res.data; // { gameId }
}

export async function getGameState(gameId: string) {
  const res = await api.get<GameStateDTO>(`/api/game/${gameId}/state`);
  return res.data;
}

export async function playCard(gameId: string, payload: PlayRequestDTO) {
  const res = await api.post<GameStateDTO>(`/api/game/${gameId}/play`, payload);
  return res.data;
}

export async function listOpenGames() {
  const res = await api.get<{ id: string; players: string[]; createdAt: string }[]>(`/api/lobby/open`);
  return res.data;
}
