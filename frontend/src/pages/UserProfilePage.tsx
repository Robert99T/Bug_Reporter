import React, { useEffect, useState } from "react";
import { User as UserIcon, Save, Trash2, AlertTriangle } from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { getUserById, updateUser, deleteUser } from "../api/userApi";
import type { UserResponse } from "../types";
import "./UserProfilePage.css";

const UserProfilePage: React.FC = () => {
  const { currentUser, logout } = useAuth();

  const [profile, setProfile] = useState<UserResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // Form fields
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [password, setPassword] = useState("");

  useEffect(() => {
    if (!currentUser) return;
    const fetchProfile = async () => {
      try {
        const res = await getUserById(currentUser.id);
        setProfile(res.data);
        setUsername(res.data.username);
        setEmail(res.data.email);
        setPhoneNumber(res.data.phoneNumber || "");
      } catch (err) {
        console.error("Failed to fetch profile:", err);
        setError("Failed to load profile.");
      } finally {
        setLoading(false);
      }
    };
    fetchProfile();
  }, [currentUser]);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentUser) return;
    setError("");
    setSuccess("");
    setSaving(true);

    try {
      const res = await updateUser(currentUser.id, {
        username: username.trim(),
        email: email.trim(),
        password,
        phoneNumber: phoneNumber.trim() || undefined,
      });
      setProfile(res.data);
      setPassword("");
      setSuccess("Profile updated successfully.");

      // Update localStorage with potentially new username
      const updatedUser = {
        ...currentUser,
        username: res.data.username,
      };
      localStorage.setItem("currentUser", JSON.stringify(updatedUser));
    } catch (err: any) {
      setError(
        err?.response?.data?.message ||
          err?.response?.data?.error ||
          "Failed to update profile."
      );
    } finally {
      setSaving(false);
    }
  };

  const handleDeleteAccount = async () => {
    if (!currentUser) return;
    if (
      !window.confirm(
        "Are you sure you want to delete your account? This action cannot be undone."
      )
    )
      return;

    try {
      await deleteUser(currentUser.id);
      logout();
    } catch (err: any) {
      setError(
        err?.response?.data?.message || "Failed to delete account."
      );
    }
  };

  if (loading) {
    return (
      <div className="profile-container">
        <p>Loading profile...</p>
      </div>
    );
  }

  if (!profile) {
    return (
      <div className="profile-container">
        <p className="profile-error">{error || "Profile not found."}</p>
      </div>
    );
  }

  return (
    <div className="profile-container">
      <div className="profile-card">
        {/* Header */}
        <div className="profile-header">
          <div className="profile-avatar">
            <UserIcon size={32} />
          </div>
          <div className="profile-header-info">
            <h1 className="profile-name">{profile.username}</h1>
            <div className="profile-meta">
              <span className={`profile-role ${profile.role.toLowerCase()}`}>
                {profile.role}
              </span>
              <span className="profile-score" title="Reputation score">
                ★ {profile.score.toFixed(1)}
              </span>
            </div>
          </div>
        </div>

        {/* Edit Form */}
        <form className="profile-form" onSubmit={handleSave}>
          <div className="profile-field">
            <label className="profile-label">Username</label>
            <input
              className="profile-input"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              minLength={3}
              maxLength={20}
            />
          </div>

          <div className="profile-field">
            <label className="profile-label">Email</label>
            <input
              className="profile-input"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="profile-field">
            <label className="profile-label">Phone number</label>
            <input
              className="profile-input"
              type="text"
              value={phoneNumber}
              onChange={(e) => setPhoneNumber(e.target.value)}
              placeholder="Optional"
            />
          </div>

          <div className="profile-field">
            <label className="profile-label">
              New password
              <span className="profile-label-hint">(required to save changes)</span>
            </label>
            <input
              className="profile-input"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              minLength={8}
              placeholder="Min 8 characters"
            />
          </div>

          {error && <p className="profile-msg error">{error}</p>}
          {success && <p className="profile-msg success">{success}</p>}

          <div className="profile-actions">
            <button
              type="submit"
              className="profile-save-btn"
              disabled={saving}
            >
              <Save size={15} />
              {saving ? "Saving..." : "Save Changes"}
            </button>
          </div>
        </form>

        {/* Danger Zone */}
        <div className="profile-danger">
          <div className="profile-danger-header">
            <AlertTriangle size={16} />
            <span>Danger Zone</span>
          </div>
          <p className="profile-danger-text">
            Deleting your account is permanent and cannot be undone.
          </p>
          <button
            className="profile-delete-btn"
            onClick={handleDeleteAccount}
          >
            <Trash2 size={15} />
            Delete Account
          </button>
        </div>
      </div>
    </div>
  );
};

export default UserProfilePage;
