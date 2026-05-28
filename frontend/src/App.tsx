import React, { createContext, useEffect, useState } from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import BugListPage from "./pages/BugListPage";
import BugDetailsPage from "./pages/BugDetailsPage";
import type { CurrentUser } from "./types";
import Navbar from "./components/Navbar";
import apiClient from "./api/client";

export const UserContext = createContext<CurrentUser | null>(null);
export const SetUserContext = createContext<React.Dispatch<React.SetStateAction<CurrentUser | null>>>(() => {});
export const RefreshUserContext = createContext<() => Promise<void>>(async () => {});

const ProtectedLayout: React.FC<{ children: React.ReactNode }> = ({ children }) => (
  <>
    <Navbar />
    {children}
  </>
);

const App: React.FC = () => {
  const [currentUser, setCurrentUser] = useState<CurrentUser | null>(null);

  useEffect(() => {
    const savedUser = localStorage.getItem("currentUser");
    if (savedUser) {
      setCurrentUser(JSON.parse(savedUser));
    }
  }, []);

  const isAuthenticated = currentUser !== null;

  const handleLogin = (user: CurrentUser) => {
    setCurrentUser(user);
  };

  const refreshCurrentUser = async () => {
    if (!currentUser) return;
    try {
      const res = await apiClient.get<CurrentUser>(`/users/${currentUser.id}`);
      setCurrentUser(res.data);
      localStorage.setItem("currentUser", JSON.stringify(res.data));
    } catch (err) {
      console.error("Failed to refresh user:", err);
    }
  };

  return (
    <UserContext.Provider value={currentUser}>
      <SetUserContext.Provider value={setCurrentUser}>
        <RefreshUserContext.Provider value={refreshCurrentUser}>
          <Router>
            <Routes>
<Route
                path="/login"
                element={<LoginPage onLogin={handleLogin} />}
              />

              <Route
                path="/"
                element={
                  isAuthenticated ? (
                    <ProtectedLayout><BugListPage /></ProtectedLayout>
                  ) : (
                    <Navigate to="/login" replace />
                  )
                }
              />

              <Route
                path="/bugs"
                element={
                  isAuthenticated ? (
                    <ProtectedLayout><BugListPage /></ProtectedLayout>
                  ) : (
                    <Navigate to="/login" replace />
                  )
                }
              />

              <Route
                path="/bugs/:id"
                element={
                  isAuthenticated ? (
                    <ProtectedLayout><BugDetailsPage /></ProtectedLayout>
                  ) : (
                    <Navigate to="/login" replace />
                  )
                }
              />
            </Routes>
          </Router>
        </RefreshUserContext.Provider>
      </SetUserContext.Provider>
    </UserContext.Provider>
  );
};

export default App;