import { api } from "./http";

export const AdminApi = {
  getGames: async () => {
    const res = await api.get("/api/admin/games");
    return res.data;
  },

  getGame: async (id: string) => {
    const res = await api.get(`/api/admin/game/${id}`);
    return res.data;
  },

  deleteGame: async (id: string) => {
    const res = await api.delete(`/api/admin/game/${id}`);
    return res.data;
  },

  resetGame: async (id: string) => {
    const res = await api.post(`/api/admin/game/${id}/reset`);
    return res.data;
  },

  createGame: async () => {
    const res = await api.post("/api/admin/game/create");
    return res.data;
  },
};
