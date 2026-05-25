import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import Navbar from "./components/Navbar";
import ProtectedRoute from "./components/ProtectedRoute";
import ModeratorRoute from "./components/ModeratorRoute";
import LoginPage from "./pages/LoginPage";
import BugListPage from "./pages/BugListPage";
import BugDetailsPage from "./pages/BugDetailsPage";
import UserProfilePage from "./pages/UserProfilePage";
import ModerationPage from "./pages/ModerationPage";

const App: React.FC = () => {
  return (
    <Router>
      <AuthProvider>
        <Navbar />
        <Routes>
          {/* Public */}
          <Route path="/login" element={<LoginPage />} />

          {/* Authenticated */}
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <BugListPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/bugs"
            element={
              <ProtectedRoute>
                <BugListPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/bugs/:id"
            element={
              <ProtectedRoute>
                <BugDetailsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/profile"
            element={
              <ProtectedRoute>
                <UserProfilePage />
              </ProtectedRoute>
            }
          />

          {/* Moderator only */}
          <Route
            path="/moderation"
            element={
              <ModeratorRoute>
                <ModerationPage />
              </ModeratorRoute>
            }
          />
        </Routes>
      </AuthProvider>
    </Router>
  );
};

export default App;