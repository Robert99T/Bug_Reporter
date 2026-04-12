import React, { useContext } from "react";
import { Pencil, Trash2, User as UserIcon } from "lucide-react";
import VoteControls from "./VoteControls";
import { UserContext } from "../App";
import type { CommentResponse, VoteType } from "../types";
import "./CommentCard.css";

interface CommentCardProps {
  comment: CommentResponse;
  onEdit: (comment: CommentResponse) => void;
  onDelete: (commentId: number) => void;
  onVote: (commentId: number, voteType: VoteType) => void;
}

const CommentCard: React.FC<CommentCardProps> = ({
  comment,
  onEdit,
  onDelete,
  onVote,
}) => {
  const currentUser = useContext(UserContext);

  const canModify =
    currentUser &&
    (currentUser.id === comment.authorId || currentUser.role === "MODERATOR");

  const isSelf = currentUser?.id === comment.authorId;

  const handleUpvote = () => {
    if (comment.userVote === "UPVOTE") {
      // Toggle off — we represent "remove" by passing the same type
      onVote(comment.id, "UPVOTE");
    } else {
      onVote(comment.id, "UPVOTE");
    }
  };

  const handleDownvote = () => {
    if (comment.userVote === "DOWNVOTE") {
      onVote(comment.id, "DOWNVOTE");
    } else {
      onVote(comment.id, "DOWNVOTE");
    }
  };

  return (
    <div className="comment-card" id={`comment-${comment.id}`}>
      <div className="comment-vote-section">
        <VoteControls
          score={comment.voteScore}
          userVote={comment.userVote}
          disabled={isSelf}
          onUpvote={handleUpvote}
          onDownvote={handleDownvote}
        />
      </div>

      <div className="comment-body">
        {/* Author row */}
        <div className="comment-header">
          <div className="comment-author">
            <UserIcon size={14} className="comment-author-icon" />
            <span className="comment-author-name">{comment.authorUsername}</span>
            <span className="comment-author-score" title="User score">
              ★ {comment.authorScore.toFixed(1)}
            </span>
          </div>

          <div className="comment-header-right">
            <span className="comment-date">
              {new Date(comment.creationDate).toLocaleDateString("en-US", {
                month: "short",
                day: "numeric",
                year: "numeric",
                hour: "2-digit",
                minute: "2-digit",
              })}
            </span>

            {canModify && (
              <div className="comment-actions">
                <button
                  className="action-btn edit-btn"
                  onClick={() => onEdit(comment)}
                  title="Edit comment"
                  aria-label="Edit comment"
                >
                  <Pencil size={14} />
                </button>
                <button
                  className="action-btn delete-btn"
                  onClick={() => onDelete(comment.id)}
                  title="Delete comment"
                  aria-label="Delete comment"
                >
                  <Trash2 size={14} />
                </button>
              </div>
            )}
          </div>
        </div>

        {/* Content */}
        <p className="comment-text">{comment.text}</p>

        {comment.pictureUrl && (
          <img
            src={comment.pictureUrl}
            alt="Comment attachment"
            className="comment-picture"
          />
        )}
      </div>
    </div>
  );
};

export default CommentCard;
