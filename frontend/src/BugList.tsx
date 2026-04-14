import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./BugList.css";
import CreateBugForm from "./components/CreateBugForm";
import { getAllBugs } from "./api/bugApi";
import type { BugResponse, CurrentUser } from "./types";

const BugList: React.FC = () => {
  const [bugs, setBugs] = useState<BugResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const savedUser = localStorage.getItem("currentUser");
  const currentUser: CurrentUser | null = savedUser
    ? JSON.parse(savedUser)
    : null;

  useEffect(() => {
    const fetchBugs = async () => {
      try {
        const res = await getAllBugs();
        setBugs(res.data);
      } catch (err) {
        console.error(err);
        setError("Nu s-au putut încărca bug-urile.");
      } finally {
        setLoading(false);
      }
    };

    fetchBugs();
  }, []);

  const getStatusClass = (status: string) => {
    switch (status) {
      case "OPEN":
        return "received";
      case "IN_PROGRESS":
        return "in-progress";
      case "SOLVED":
        return "solved";
      default:
        return "received";
    }
  };

  if (loading) return <p>Loading bugs...</p>;
  if (error) return <p style={{ color: "red" }}>{error}</p>;

  return (
    <div className="bug-list-container">
      <h2 className="bug-list-title">Bug Reports</h2>

      {currentUser && (
        <CreateBugForm
          authorId={currentUser.id}
          onBugCreated={(newBug) => setBugs((prev) => [newBug, ...prev])}
        />
      )}

      {bugs.length === 0 ? (
        <p style={{ textAlign: "center" }}>No bugs found.</p>
      ) : (
        bugs.map((bug) => (
          <div
            key={bug.id}
            className="bug-card bug-markdown bug-card-clickable"
            onClick={() => navigate(`/bugs/${bug.id}`)}
            role="button"
            tabIndex={0}
            onKeyDown={(e) => {
              if (e.key === "Enter" || e.key === " ") {
                navigate(`/bugs/${bug.id}`);
              }
            }}
          >
            <h3 className="bug-title">{bug.title}</h3>

            <div className="bug-header">
              <div className="bug-user">
                By <strong>{bug.authorUsername || "Unknown"}</strong>
              </div>

              <div className="bug-meta">
                <span className="bug-date-inline">
                  {new Date(bug.creationDate).toLocaleString()}
                </span>

                <span className={`bug-status ${getStatusClass(bug.status)}`}>
                  {bug.status.replace("_", " ")}
                </span>
              </div>
            </div>

            <p className="bug-text">{bug.text}</p>

            {bug.pictureUrl && (
              <img
                src={bug.pictureUrl}
                alt="Bug screenshot"
                className="bug-picture"
              />
            )}

            {bug.tags && bug.tags.length > 0 && (
              <div className="bug-tags">
                {bug.tags.map((tag, index) => (
                  <span key={index} className="bug-tag">
                    #{tag}
                  </span>
                ))}
              </div>
            )}

            <p className="bug-comments">
              {bug.comments?.length || 0} comment
              {(bug.comments?.length || 0) !== 1 ? "s" : ""}
            </p>
          </div>
        ))
      )}
    </div>
  );
};

export default BugList;