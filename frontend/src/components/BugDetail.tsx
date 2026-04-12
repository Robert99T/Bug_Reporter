import React, { useContext } from "react";
import {
  User as UserIcon,
  Calendar,
  Pencil,
  Trash2,
  CheckCircle,
} from "lucide-react";
import VoteControls from "./VoteControls";
import { UserContext } from "../App";
import type { BugResponse, VoteType } from "../types";
import "./BugDetail.css";

interface BugDetailProps {
  bug: BugResponse;
  onEdit: () => void;
  onDelete: () => void;
  onMarkSolved: () => void;
  onVote: (voteType: VoteType) => void;
}

const getStatusLabel = (status: string) => {
  switch (status) {
    case "OPEN":
      return "Received";
    case "IN_PROGRESS":
      return "In Progress";
    case "SOLVED":
      return "Solved";
    default:
      return status;
  }
};

const getStatusClass = (status: string) => {
  switch (status) {
    case "OPEN":
      return "status-received";
    case "IN_PROGRESS":
      return "status-in-progress";
    case "SOLVED":
      return "status-solved";
    default:
      return "status-received";
  }
};

const BugDetail: React.FC<BugDetailProps> = ({
  bug,
  onEdit,
  onDelete,
  onMarkSolved,
  onVote,
}) => {
  const currentUser = useContext(UserContext);

  const canModify =
    currentUser &&
    (currentUser.id === bug.authorId || currentUser.role === "MODERATOR");

  const isBugAuthor = currentUser?.id === bug.authorId;
  const isSelf = currentUser?.id === bug.authorId;

  return (
    <div className="bug-detail">
      {/* Vote column + Content */}
      <div className="bug-detail-layout">
        <div className="bug-detail-vote-col">
          <VoteControls
            score={bug.voteScore}
            userVote={bug.userVote}
            disabled={isSelf}
            onUpvote={() => onVote("UPVOTE")}
            onDownvote={() => onVote("DOWNVOTE")}
          />
        </div>

        <div className="bug-detail-content">
          {/* Status + Actions bar */}
          <div className="bug-detail-topbar">
            <span className={`bug-detail-status ${getStatusClass(bug.status)}`}>
              {getStatusLabel(bug.status)}
            </span>

            {canModify && (
              <div className="bug-detail-actions">
                <button
                  className="action-btn edit-btn"
                  onClick={onEdit}
                  title="Edit bug"
                >
                  <Pencil size={15} />
                </button>
                <button
                  className="action-btn delete-btn"
                  onClick={onDelete}
                  title="Delete bug"
                >
                  <Trash2 size={15} />
                </button>
              </div>
            )}
          </div>

          {/* Title */}
          <h1 className="bug-detail-title">{bug.title}</h1>

          {/* Author bar */}
          <div className="bug-detail-author-bar">
            <div className="bug-detail-author">
              <UserIcon size={15} className="bug-detail-author-icon" />
              <span className="bug-detail-author-name">{bug.authorUsername}</span>
              <span className="bug-detail-author-score" title="User score">
                ★ {bug.authorScore.toFixed(1)}
              </span>
            </div>
            <div className="bug-detail-date">
              <Calendar size={13} />
              <span>
                {new Date(bug.creationDate).toLocaleDateString("en-US", {
                  month: "long",
                  day: "numeric",
                  year: "numeric",
                  hour: "2-digit",
                  minute: "2-digit",
                })}
              </span>
            </div>
          </div>

          {/* Body text */}
          <div className="bug-detail-body">
            <p className="bug-detail-text">{bug.text}</p>
          </div>

          {/* Image */}
          {bug.pictureUrl && (
            <img
              src={bug.pictureUrl}
              alt="Bug screenshot"
              className="bug-detail-picture"
            />
          )}

          {/* Tags */}
          {bug.tags && bug.tags.length > 0 && (
            <div className="bug-detail-tags">
              {bug.tags.map((tag, i) => (
                <span key={i} className="bug-detail-tag">
                  #{tag}
                </span>
              ))}
            </div>
          )}

          {/* Accept & Mark Solved — visible only for bug author when not already solved */}
          {isBugAuthor && bug.status !== "SOLVED" && (
            <button className="bug-detail-solve-btn" onClick={onMarkSolved}>
              <CheckCircle size={16} />
              Accept &amp; Mark Solved
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default BugDetail;
