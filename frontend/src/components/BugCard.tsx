import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { ChevronUp, ChevronDown } from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { voteBug } from "../api/voteApi";
import type { BugResponse, VoteType } from "../types";
import "./BugCard.css";

interface BugCardProps {
  bug: BugResponse;
  onVoteChange?: (bugId: number, newVoteScore: number, newUserVote: VoteType | null) => void;
}

const getStatusClass = (status: string): string => {
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

const BugCard: React.FC<BugCardProps> = ({ bug: initialBug, onVoteChange }) => {
  const navigate = useNavigate();
  const { currentUser } = useAuth();
  const [bug, setBug] = useState(initialBug);
  const [voting, setVoting] = useState(false);

  const isSelf = currentUser?.id === bug.authorId;

  const handleVote = async (voteType: VoteType) => {
    if (!currentUser || isSelf || voting) return;

    setVoting(true);
    try {
      let newUserVote: VoteType | null;
      let newVoteScore = bug.voteScore;

      if (bug.userVote === null || bug.userVote === undefined) {
        // No existing vote → cast new
        newUserVote = voteType;
        newVoteScore += voteType === "UPVOTE" ? 1 : -1;
      } else if (bug.userVote === voteType) {
        // Same vote → toggle off
        newUserVote = null;
        newVoteScore += voteType === "UPVOTE" ? -1 : 1;
      } else {
        // Opposite vote → switch
        newUserVote = voteType;
        newVoteScore += voteType === "UPVOTE" ? 2 : -2;
      }

      await voteBug(bug.id, { userId: currentUser.id, voteType });

      const newBug = { ...bug, voteScore: newVoteScore, userVote: newUserVote };
      setBug(newBug);

      if (onVoteChange) {
        onVoteChange(bug.id, newVoteScore, newUserVote);
      }
    } catch (err) {
      console.error("Failed to vote:", err);
    } finally {
      setVoting(false);
    }
  };

  const handleCardClick = () => {
    navigate(`/bugs/${bug.id}`);
  };

  return (
    <div className="bug-card bug-markdown" onClick={handleCardClick}>
      <div className="bug-card-layout">
        <div className="bug-card-vote-col" onClick={(e) => e.stopPropagation()}>
          <button
            className={`vote-btn vote-up ${bug.userVote === "UPVOTE" ? "active" : ""}`}
            onClick={() => handleVote("UPVOTE")}
            disabled={isSelf}
            title={isSelf ? "Cannot vote on your own bug" : "Upvote"}
          >
            <ChevronUp size={22} />
          </button>
          <span className={`vote-score ${bug.voteScore > 0 ? "positive" : bug.voteScore < 0 ? "negative" : ""}`}>
            {bug.voteScore}
          </span>
          <button
            className={`vote-btn vote-down ${bug.userVote === "DOWNVOTE" ? "active" : ""}`}
            onClick={() => handleVote("DOWNVOTE")}
            disabled={isSelf}
            title={isSelf ? "Cannot vote on your own bug" : "Downvote"}
          >
            <ChevronDown size={22} />
          </button>
        </div>

        <div className="bug-card-content">
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
      </div>
    </div>
  );
};

export default BugCard;
