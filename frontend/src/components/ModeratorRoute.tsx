import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

interface ModeratorRouteProps {
  children: React.ReactNode;
}

const ModeratorRoute: React.FC<ModeratorRouteProps> = ({ children }) => {
  const { isAuthenticated, isModerator } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (!isModerator) {
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
};

export default ModeratorRoute;
