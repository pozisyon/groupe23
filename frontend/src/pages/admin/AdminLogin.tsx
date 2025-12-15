import { useState } from "react";
import { api } from "../../api/http";
import { useAuthStore } from "../../store/authStore";

export default function AdminLogin() {
  const [u, setU] = useState("");
  const [p, setP] = useState("");
  const [err, setErr] = useState("");
  const setUser = useAuthStore((s) => s.setUser);

  async function submit() {
    try {
      const res = await api.post("/api/admin/login", {
        username: u,
        password: p,
      });

      setUser({
        handle: res.data.username ?? "admin",
        role: res.data.role,
        token: res.data.token,
      });

      window.location.href = "/admin";
    } catch (e: any) {
      setErr("Identifiants invalides");
    }
  }

  return (
    <div className="h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-6 rounded shadow w-80 space-y-4">
        <h1 className="text-xl font-semibold">Admin Login</h1>

        {err && <div className="text-red-500 text-sm">{err}</div>}

        <input
          className="w-full border p-2 rounded"
          placeholder="Nom d'utilisateur"
          value={u}
          onChange={(e) => setU(e.target.value)}
        />
        <input
          className="w-full border p-2 rounded"
          type="password"
          placeholder="Mot de passe"
          value={p}
          onChange={(e) => setP(e.target.value)}
        />
        <button
          className="w-full p-2 bg-black text-white rounded"
          onClick={submit}
        >
          Se connecter
        </button>
      </div>
    </div>
  );
}
