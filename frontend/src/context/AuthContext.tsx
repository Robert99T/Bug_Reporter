import React, { createContext, useContext, useState, useEffect, useCallback } from "react";
import type { CurrentUser } from "../types";
import { loginUser as apiLogin } from "../api/authApi";
import { getUserScore } from "../api/userApi";
import apiClient from "../api/client"; // <-- Added this to make the teammate's fetch work

// ─── Context Shape ───────────────────────────────────────────────────
interface AuthContextValue {
  currentUser: CurrentUser | null;
  isAuthenticated: boolean;
  isModerator: boolean;
  login: (username: string, password: string) => Promise<CurrentUser>;
  logout: () => void;
  refreshUser: () => Promise<void>; // <-- 1. Added to the interface
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

// ─── Provider ────────────────────────────────────────────────────────
export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [currentUser, setCurrentUser] = useState<CurrentUser | null>(null);

  // Restore session from localStorage on mount & validate it
  useEffect(() => {
    const saved = localStorage.getItem("currentUser");
    if (!saved) return;

    try {
      const user: CurrentUser = JSON.parse(saved);
      setCurrentUser(user);

      // Lightweight session validation
      getUserScore(user.id).catch(() => {
        // Session is stale → clear
        localStorage.removeItem("currentUser");
        setCurrentUser(null);
      });
    } catch {
      localStorage.removeItem("currentUser");
    }
  }, []);

  const login = useCallback(async (username: string, password: string) => {
    const res = await apiLogin(username, password);
    const user = res.data;
    setCurrentUser(user);
    localStorage.setItem("currentUser", JSON.stringify(user));
    return user;
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem("currentUser");
    setCurrentUser(null);
    window.location.href = "/login";
  }, []);

  // 2. Teammate's refresh function integrated cleanly here
  const refreshUser = useCallback(async () => {
    if (!currentUser) return;
    try {
      const res = await apiClient.get<CurrentUser>(`/users/${currentUser.id}`);
      setCurrentUser(res.data);
      localStorage.setItem("currentUser", JSON.stringify(res.data));
    } catch (err) {
      console.error("Failed to refresh user:", err);
    }
  }, [currentUser]);

  const value: AuthContextValue = {
    currentUser,
    isAuthenticated: currentUser !== null,
    isModerator: currentUser?.role === "MODERATOR",
    login,
    logout,
    refreshUser, // 3. Exposing it so useAuth() can give it to BugCard
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

// ─── Hook ────────────────────────────────────────────────────────────
export const useAuth = (): AuthContextValue => {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return ctx;
};

export default AuthContext;