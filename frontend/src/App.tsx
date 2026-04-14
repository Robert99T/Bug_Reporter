import React, { createContext, useState } from "react";
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
          <Route
            path="/login"
            element={<LoginPage onLogin={handleLogin} />}
          />

          <Route
            path="/"
            element={
              isAuthenticated ? <BugListPage /> : <Navigate to="/login" replace />
            }
          />

          <Route
            path="/bugs"
            element={
              isAuthenticated ? <BugListPage /> : <Navigate to="/login" replace />
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
