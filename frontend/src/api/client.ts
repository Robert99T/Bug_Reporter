import axios from "axios";

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080",
  withCredentials: true,
  headers: { "Content-Type": "application/json" },
});

// ─── Response Interceptor ────────────────────────────────────────────
// Centralized handling for authentication / authorization errors.
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (!error.response) {
      // Network error — server unreachable
      return Promise.reject(error);
    }

    const { status, data } = error.response;

    if (status === 401) {
      // Session expired or not authenticated
      localStorage.removeItem("currentUser");
      // Avoid redirect loop if already on /login
      if (window.location.pathname !== "/login") {
        window.location.href = "/login";
      }
    }

    if (status === 403 && data?.error === "ACCOUNT_BANNED") {
      // User has been banned — force logout
      localStorage.removeItem("currentUser");
      if (window.location.pathname !== "/login") {
        window.location.href = "/login";
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;
