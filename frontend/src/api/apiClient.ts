import axios from "axios";

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080",
  withCredentials: true, // si tu utilises des cookies httpOnly
});

// Intercepteur (ex: JWT dans Authorization si besoin)
// api.interceptors.request.use(cfg => {
//   const token = localStorage.getItem("jwt");
//   if (token) cfg.headers.Authorization = `Bearer ${token}`;
//   return cfg;
// });
