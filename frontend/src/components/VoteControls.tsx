import React from "react";
import { ChevronUp, ChevronDown } from "lucide-react";
import type { VoteType } from "../types";
import "./VoteControls.css";

interface VoteControlsProps {
  score: number;
  userVote?: VoteType | null;
  disabled?: boolean;          // true when user owns this item
  onUpvote: () => void;
  onDownvote: () => void;
}

const VoteControls: React.FC<VoteControlsProps> = ({
  score,
  userVote,
  disabled = false,
  onUpvote,
  onDownvote,
}) => {
  return (
    <div className="vote-controls" aria-label="Vote controls">
      <button
        className={`vote-btn vote-up ${userVote === "UPVOTE" ? "active" : ""}`}
        onClick={onUpvote}
        disabled={disabled}
        title={disabled ? "You cannot vote on your own post" : "Upvote"}
        aria-label="Upvote"
      >
        <ChevronUp size={20} />
      </button>

      <span className={`vote-score ${score > 0 ? "positive" : score < 0 ? "negative" : ""}`}>
        {score}
      </span>

      <button
        className={`vote-btn vote-down ${userVote === "DOWNVOTE" ? "active" : ""}`}
        onClick={onDownvote}
        disabled={disabled}
        title={disabled ? "You cannot vote on your own post" : "Downvote"}
        aria-label="Downvote"
      >
        <ChevronDown size={20} />
      </button>
    </div>
  );
};

export default VoteControls;
