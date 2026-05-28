import React from "react";
import { Link, useLocation } from "react-router-dom";
import { Bug, User, Shield, LogOut } from "lucide-react";
import { useAuth } from "../context/AuthContext";
import "./Navbar.css";

const Navbar: React.FC = () => {
  const { currentUser, isModerator, logout } = useAuth();
  const location = useLocation();

  if (!currentUser) return null;

  const isActive = (path: string) =>
    location.pathname === path ? "nav-link active" : "nav-link";

  return (
    <nav className="navbar" aria-label="Main navigation">
      <div className="navbar-inner">
        {/* Left — Brand + Links */}
        <div className="navbar-left">
          <Link to="/" className="navbar-brand">
            <Bug size={20} />
            <span>Bug Reporter</span>
          </Link>

          <div className="navbar-links">
            <Link to="/bugs" className={isActive("/bugs")}>
              Bugs
            </Link>
            <Link to="/profile" className={isActive("/profile")}>
              <User size={15} />
              Profile
            </Link>
            {isModerator && (
              <Link to="/moderation" className={isActive("/moderation")}>
                <Shield size={15} />
                Moderation
              </Link>
            )}
          </div>
        </div>

        {/* Right — User info + Logout */}
        <div className="navbar-right">
          <span className="navbar-user">
            {currentUser.username}
            {/* Teammate's score feature injected here */}
            <span className="navbar-badge" style={{ marginLeft: '8px' }}>
              ★ {(currentUser.score ?? 0).toFixed(1)}
            </span>
            {isModerator && <span className="navbar-score">MOD</span>}
          </span>
          <button className="navbar-logout" onClick={logout} title="Logout">
            <LogOut size={16} />
            Logout
          </button>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;