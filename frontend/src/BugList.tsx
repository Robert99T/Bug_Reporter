import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "./BugList.css";

interface Comment {
  id: number;
  text: string;
  pictureUrl?: string | null;
  creationDate: string;
  authorId: number;
  authorUsername: string;
  bugId: number;
}

interface Bug {
  id: number;
  title: string;
  text: string;
  creationDate: string;
  pictureUrl?: string | null;
  status: "OPEN" | "IN_PROGRESS" | "SOLVED";
  authorId: number;
  authorUsername: string;
  comments?: Comment[];
  tags?: string[];
}

const BugList: React.FC = () => {
  const [bugs, setBugs] = useState<Bug[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const fetchBugs = async () => {
      try {
        const res = await axios.get<Bug[]>("http://localhost:8080/bugs", {
          withCredentials: true,
        });
        setBugs(res.data);
      } catch (err) {
        console.error(err);
        setError("Failed to fetch bugs");
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
              if (e.key === "Enter" || e.key === " ") navigate(`/bugs/${bug.id}`);
            }}
          >
            <h3 className="bug-title">{bug.title}</h3>

            {/* HEADER */}
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

            {/* IMAGE */}
            {bug.pictureUrl && (
              <img
                src={bug.pictureUrl}
                alt="Bug screenshot"
                className="bug-picture"
              />
            )}

            {/* TAGS */}
            {bug.tags && bug.tags.length > 0 && (
              <div className="bug-tags">
                {bug.tags.map((tag, index) => (
                  <span key={index} className="bug-tag">
                    #{tag}
                  </span>
                ))}
              </div>
            )}

            {/* COMMENTS */}
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