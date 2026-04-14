import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import type { CurrentUser } from "./types";
import "./Login.css";

interface LoginProps {
  onLogin: (user: CurrentUser) => void;
}

const Login: React.FC<LoginProps> = ({ onLogin }) => {
  const [mode, setMode] = useState<"login" | "register">("login");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  const resetMessages = () => {
    setError("");
    setSuccess("");
  };

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    resetMessages();
    setLoading(true);

    try {
      const res = await axios.post<CurrentUser>(
        "http://localhost:8080/auth/login",
        { username, password },
        { withCredentials: true }
      );

      onLogin(res.data);
      localStorage.setItem("currentUser", JSON.stringify(res.data));
      navigate("/");
    } catch (err) {
      console.error(err);
      setError("Username sau parolă invalidă.");
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    resetMessages();
    setLoading(true);

    try {
      await axios.post(
        "http://localhost:8080/users/register",
        {
          username,
          email,
          password,
          phoneNumber: phoneNumber || undefined,
        },
        { withCredentials: true }
      );

      setSuccess("Cont creat cu succes. Te poți loga acum.");
      setMode("login");
    } catch (err: any) {
      console.error(err);
      setError(
        err?.response?.data?.message ||
          err?.response?.data?.error ||
          "Nu s-a putut crea contul."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <h2 className="login-heading">
          {mode === "login" ? "Login" : "Creează cont"}
        </h2>

        <div className="login-toggle">
          <button
            type="button"
            className={`login-toggle-button ${mode === "login" ? "active" : ""}`}
            onClick={() => {
              setMode("login");
              resetMessages();
            }}
          >
            Login
          </button>
          <button
            type="button"
            className={`login-toggle-button ${mode === "register" ? "active" : ""}`}
            onClick={() => {
              setMode("register");
              resetMessages();
            }}
          >
            Create account
          </button>
        </div>

        <form
          className="login-form"
          onSubmit={mode === "login" ? handleLogin : handleRegister}
        >
          <div className="login-group">
            <label className="login-label">Username</label>
            <input
              className="login-input"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>

          {mode === "register" && (
            <>
              <div className="login-group">
                <label className="login-label">Email</label>
                <input
                  className="login-input"
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
              </div>

              <div className="login-group">
                <label className="login-label">Phone number</label>
                <input
                  className="login-input"
                  type="text"
                  value={phoneNumber}
                  onChange={(e) => setPhoneNumber(e.target.value)}
                />
              </div>
            </>
          )}

          <div className="login-group">
            <label className="login-label">Parolă</label>
            <input
              className="login-input"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              minLength={8}
            />
          </div>

          {error && <p className="login-error">{error}</p>}
          {success && <p className="login-success">{success}</p>}

          <button className="login-submit" type="submit" disabled={loading}>
            {loading
              ? mode === "login"
                ? "Se face login..."
                : "Se creează contul..."
              : mode === "login"
              ? "Login"
              : "Create account"}
          </button>
        </form>
      </div>
    </div>
  );
};

export default Login;