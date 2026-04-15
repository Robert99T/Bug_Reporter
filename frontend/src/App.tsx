import React, { createContext, useEffect, useState } from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import Login from "./Login";
import BugList from "./BugList";
import BugDetailsPage from "./pages/BugDetailsPage";
import type { CurrentUser } from "./types";

export const UserContext = createContext<CurrentUser | null>(null);

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

  return (
    <UserContext.Provider value={currentUser}>
      <Router>
        <Routes>
          <Route path="/login" element={<Login onLogin={handleLogin} />} />

          <Route
            path="/"
            element={
              isAuthenticated ? <BugList /> : <Navigate to="/login" replace />
            }
          />

          <Route
            path="/bugs"
            element={
              isAuthenticated ? <BugList /> : <Navigate to="/login" replace />
            }
          />

          <Route
            path="/bugs/:id"
            element={
              isAuthenticated ? (
                <BugDetailsPage />
              ) : (
                <Navigate to="/login" replace />
              )
            }
          />
        </Routes>
      </Router>
    </UserContext.Provider>
  );
};

export default App;