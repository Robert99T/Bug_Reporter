import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { registerUser } from "../api/authApi";
import "../Login.css";

const LoginPage: React.FC = () => {
  const [mode, setMode] = useState<"login" | "register">("login");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
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
      await login(username, password);
      navigate("/");
    } catch (err: any) {
      const status = err?.response?.status;
      const data = err?.response?.data;

      if (status === 403 && data?.error === "ACCOUNT_BANNED") {
        setError(data.message || "Your account has been banned. Please contact an administrator.");
      } else {
        setError("Invalid username or password.");
      }
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    resetMessages();
    setLoading(true);

    try {
      await registerUser({
        username,
        email,
        password,
        phoneNumber: phoneNumber || undefined,
      });

      setSuccess("Account created successfully. You can now log in.");
      setMode("login");
    } catch (err: any) {
      setError(
        err?.response?.data?.message ||
          err?.response?.data?.error ||
          "Failed to create account."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <h2 className="login-heading">
          {mode === "login" ? "Login" : "Create Account"}
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
            <label className="login-label">Password</label>
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
                ? "Logging in..."
                : "Creating account..."
              : mode === "login"
              ? "Login"
              : "Create account"}
          </button>
        </form>
      </div>
    </div>
  );
};

export default LoginPage;