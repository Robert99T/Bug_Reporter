import React, { createContext, useState } from "react";
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

// ─── User Context ────────────────────────────────────────────────────
// Provides the logged-in user's info to all child components.
export const UserContext = createContext<CurrentUser | null>(null);

const App: React.FC = () => {
  const [currentUser, setCurrentUser] = useState<CurrentUser | null>(null);

  const isAuthenticated = currentUser !== null;

  const handleLogin = (user: CurrentUser) => {
    setCurrentUser(user);
  };

  return (
    <UserContext.Provider value={currentUser}>
      <Router>
        <Routes>
          {/* Login page */}
          <Route
            path="/login"
            element={<Login onLogin={handleLogin} />}
          />

          {/* Home — bug list */}
          <Route
            path="/"
            element={
              isAuthenticated ? <BugList /> : <Navigate to="/login" replace />
            }
          />

          {/* Bug list (explicit) */}
          <Route
            path="/bugs"
            element={
              isAuthenticated ? <BugList /> : <Navigate to="/login" replace />
            }
          />

          {/* Bug details page */}
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