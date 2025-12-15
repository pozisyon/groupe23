import { useAuthStore } from "../store/authStore";
import { Navigate } from "react-router-dom";

export function AdminGuard({ children }) {
  const user = useAuthStore((s) => s.user);

  if (!user || user.role !== "ADMIN") {
    return <Navigate to="/admin/login" replace />;
  }

  return children;
}
