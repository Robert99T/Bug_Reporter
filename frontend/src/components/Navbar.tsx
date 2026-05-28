import React, { useContext } from "react";
import { useNavigate } from "react-router-dom";
import { UserContext } from "../App";
import "./Navbar.css";

const Navbar: React.FC = () => {
    const currentUser = useContext(UserContext);
    const navigate = useNavigate();
    const handleLogout = () => {
        localStorage.removeItem("currentUser");
        navigate("/login");
    };
    return (
        <nav className="navbar">
            <div className="navbar-left">
                <span className="navbar-logo">🐛 Bug Reporter</span>
            </div>
            <div className="navbar-center">
                {currentUser && (
                    <span className="navbar-user">
            {currentUser.username}
                        <span className="navbar-score"> ★ {(currentUser.score ?? 0).toFixed(1)}</span>
          </span>
                )}
            </div>
            <div className="navbar-right">
                <button className="navbar-logout" onClick={handleLogout}>
                    Logout
                </button>
            </div>
        </nav>
    );
};
export default Navbar;