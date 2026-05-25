import React, { useEffect, useState } from "react";
import { Shield, Ban, CheckCircle, AlertTriangle } from "lucide-react";
import { getAllUsers } from "../api/userApi";
import { banUser, unbanUser } from "../api/moderationApi";
import { useAuth } from "../context/AuthContext";
import type { UserResponse } from "../types";
import "./ModerationPage.css";

const ModerationPage: React.FC = () => {
  const { currentUser } = useAuth();
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [actionLoading, setActionLoading] = useState<number | null>(null);
  const [feedback, setFeedback] = useState<{ type: "success" | "error"; message: string } | null>(null);

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const res = await getAllUsers();
      setUsers(res.data);
      setError("");
    } catch (err) {
      console.error("Failed to fetch users:", err);
      setError("Failed to load users.");
    } finally {
      setLoading(false);
    }
  };

  const handleBan = async (userId: number) => {
    if (!window.confirm("Are you sure you want to ban this user?")) return;
    setActionLoading(userId);
    setFeedback(null);
    try {
      const res = await banUser(userId);
      setFeedback({ type: "success", message: res.data.message });
      await fetchUsers();
    } catch (err: any) {
      setFeedback({
        type: "error",
        message: err?.response?.data?.message || err?.response?.data?.error || "Failed to ban user.",
      });
    } finally {
      setActionLoading(null);
    }
  };

  const handleUnban = async (userId: number) => {
    setActionLoading(userId);
    setFeedback(null);
    try {
      const res = await unbanUser(userId);
      setFeedback({ type: "success", message: res.data.message });
      await fetchUsers();
    } catch (err: any) {
      setFeedback({
        type: "error",
        message: err?.response?.data?.message || err?.response?.data?.error || "Failed to unban user.",
      });
    } finally {
      setActionLoading(null);
    }
  };

  if (loading) {
    return (
      <div className="mod-container">
        <p>Loading users...</p>
      </div>
    );
  }

  return (
    <div className="mod-container">
      <div className="mod-header">
        <Shield size={24} />
        <h1 className="mod-title">Moderation Dashboard</h1>
      </div>

      {error && <p className="mod-error">{error}</p>}

      {feedback && (
        <div className={`mod-feedback ${feedback.type}`}>
          {feedback.type === "success" ? (
            <CheckCircle size={16} />
          ) : (
            <AlertTriangle size={16} />
          )}
          {feedback.message}
        </div>
      )}

      <div className="mod-table-wrapper">
        <table className="mod-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Username</th>
              <th>Email</th>
              <th>Role</th>
              <th>Score</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.id} className={user.banned ? "banned-row" : ""}>
                <td className="mod-cell-id">{user.id}</td>
                <td className="mod-cell-username">
                  {user.username}
                  {user.id === currentUser?.id && (
                    <span className="mod-you-badge">You</span>
                  )}
                </td>
                <td>{user.email}</td>
                <td>
                  <span className={`mod-role-badge ${user.role.toLowerCase()}`}>
                    {user.role}
                  </span>
                </td>
                <td>{user.score.toFixed(1)}</td>
                <td>
                  {user.banned ? (
                    <span className="mod-status-banned">
                      <Ban size={13} /> Banned
                    </span>
                  ) : (
                    <span className="mod-status-active">Active</span>
                  )}
                </td>
                <td>
                  {user.id !== currentUser?.id && (
                    <>
                      {user.banned ? (
                        <button
                          className="mod-action-btn unban"
                          onClick={() => handleUnban(user.id)}
                          disabled={actionLoading === user.id}
                        >
                          {actionLoading === user.id ? "..." : "Unban"}
                        </button>
                      ) : (
                        <button
                          className="mod-action-btn ban"
                          onClick={() => handleBan(user.id)}
                          disabled={actionLoading === user.id}
                        >
                          {actionLoading === user.id ? "..." : "Ban"}
                        </button>
                      )}
                    </>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default ModerationPage;
