import React, { useEffect, useState, useCallback, useContext } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { ArrowLeft, MessageSquare } from "lucide-react";
import BugDetail from "../components/BugDetail";
import CommentCard from "../components/CommentCard";
import CommentForm from "../components/CommentForm";
import EditBugModal from "../components/EditBugModal";
import { UserContext } from "../App";
import { getBugById, updateBug, deleteBug } from "../api/bugApi";
import {
  getCommentsByBugId,
  createComment,
  updateComment,
  deleteComment,
} from "../api/commentApi";
import { voteBug, voteComment } from "../api/voteApi";
import type {
  BugResponse,
  CommentResponse,
  UpdateBugRequest,
  VoteType,
} from "../types";
import "./BugDetailsPage.css";

const BugDetailsPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const currentUser = useContext(UserContext);

  const [bug, setBug] = useState<BugResponse | null>(null);
  const [comments, setComments] = useState<CommentResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // Edit states
  const [showEditModal, setShowEditModal] = useState(false);
  const [editingComment, setEditingComment] = useState<CommentResponse | null>(
    null
  );

  const bugId = Number(id);

  // ─── Fetch Data ────────────────────────────────────────────────────
  const fetchBug = useCallback(async () => {
    try {
      const res = await getBugById(bugId, currentUser?.id);
      setBug(res.data);
    } catch (err) {
      console.error("Failed to fetch bug:", err);
      setError("Bug not found or failed to load.");
    }
  }, [bugId, currentUser?.id]);

  const fetchComments = useCallback(async () => {
    try {
      const res = await getCommentsByBugId(bugId, currentUser?.id);
      // Sort by vote score descending
      const sorted = [...res.data].sort((a, b) => b.voteScore - a.voteScore);
      setComments(sorted);
    } catch (err) {
      console.error("Failed to fetch comments:", err);
    }
  }, [bugId, currentUser?.id]);

  useEffect(() => {
    const loadAll = async () => {
      setLoading(true);
      await Promise.all([fetchBug(), fetchComments()]);
      setLoading(false);
    };
    loadAll();
  }, [fetchBug, fetchComments]);

  // ─── Bug Actions ───────────────────────────────────────────────────
  const handleEditBug = async (data: UpdateBugRequest) => {
    try {
      const res = await updateBug(bugId, data);
      setBug(res.data);
      setShowEditModal(false);
    } catch (err) {
      console.error("Failed to update bug:", err);
    }
  };

  const handleDeleteBug = async () => {
    if (!window.confirm("Are you sure you want to delete this bug report?"))
      return;
    try {
      await deleteBug(bugId);
      navigate("/bugs");
    } catch (err) {
      console.error("Failed to delete bug:", err);
    }
  };

  const handleMarkSolved = async () => {
    if (
      !window.confirm(
        "Mark this bug as solved? No new comments will be allowed."
      )
    )
      return;
    try {
      const res = await updateBug(bugId, { status: "SOLVED" });
      setBug(res.data);
    } catch (err) {
      console.error("Failed to mark bug as solved:", err);
    }
  };

  // ─── Bug Voting ────────────────────────────────────────────────────
  const handleBugVote = async (voteType: VoteType) => {
    if (!currentUser) return;
    try {
      await voteBug(bugId, { userId: currentUser.id, voteType });
      await fetchBug(); // Refresh to get updated score + userVote
    } catch (err) {
      console.error("Failed to vote on bug:", err);
    }
  };

  // ─── Comment Actions ──────────────────────────────────────────────
  const handleCreateComment = async (text: string, pictureUrl?: string) => {
    if (!currentUser) return;
    try {
      await createComment(bugId, {
        text,
        authorId: currentUser.id,
        pictureUrl,
      });
      await fetchComments();
      await fetchBug(); // Refresh bug in case status changed to IN_PROGRESS
    } catch (err) {
      console.error("Failed to create comment:", err);
    }
  };

  const handleEditComment = async (text: string, pictureUrl?: string) => {
    if (!editingComment) return;
    try {
      await updateComment(editingComment.id, { text, pictureUrl });
      setEditingComment(null);
      await fetchComments();
    } catch (err) {
      console.error("Failed to update comment:", err);
    }
  };

  const handleDeleteComment = async (commentId: number) => {
    if (!window.confirm("Delete this comment?")) return;
    try {
      await deleteComment(commentId);
      await fetchComments();
    } catch (err) {
      console.error("Failed to delete comment:", err);
    }
  };

  // ─── Comment Voting ───────────────────────────────────────────────
  const handleCommentVote = async (commentId: number, voteType: VoteType) => {
    if (!currentUser) return;
    try {
      await voteComment(commentId, { userId: currentUser.id, voteType });
      await fetchComments();
    } catch (err) {
      console.error("Failed to vote on comment:", err);
    }
  };

  // ─── Render ────────────────────────────────────────────────────────
  if (loading) {
    return (
      <div className="bdp-container">
        <div className="bdp-skeleton">
          <div className="skeleton-block skeleton-title" />
          <div className="skeleton-block skeleton-author" />
          <div className="skeleton-block skeleton-body" />
          <div className="skeleton-block skeleton-body-short" />
        </div>
      </div>
    );
  }

  if (error || !bug) {
    return (
      <div className="bdp-container">
        <div className="bdp-error">
          <h2>Oops!</h2>
          <p>{error || "Bug not found."}</p>
          <button className="bdp-back-btn" onClick={() => navigate("/bugs")}>
            <ArrowLeft size={16} />
            Back to Bugs
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="bdp-container">
      {/* Back navigation */}
      <button className="bdp-back-btn" onClick={() => navigate("/bugs")}>
        <ArrowLeft size={16} />
        All Bug Reports
      </button>

      {/* Bug detail card */}
      <BugDetail
        bug={bug}
        onEdit={() => setShowEditModal(true)}
        onDelete={handleDeleteBug}
        onMarkSolved={handleMarkSolved}
        onVote={handleBugVote}
      />

      {/* Comments section */}
      <div className="bdp-comments-section">
        <div className="bdp-comments-header">
          <MessageSquare size={18} />
          <h3 className="bdp-comments-title">
            {comments.length} Comment{comments.length !== 1 && "s"}
          </h3>
          <span className="bdp-comments-sort-label">Sorted by votes</span>
        </div>

        {/* Comment list */}
        <div className="bdp-comments-list">
          {comments.length === 0 ? (
            <p className="bdp-no-comments">
              No comments yet. Be the first to respond!
            </p>
          ) : (
            comments.map((c) => (
              <CommentCard
                key={c.id}
                comment={c}
                onEdit={(comment) => setEditingComment(comment)}
                onDelete={handleDeleteComment}
                onVote={handleCommentVote}
              />
            ))
          )}
        </div>

        {/* Comment form */}
        {editingComment ? (
          <CommentForm
            bugStatus={bug.status}
            initialText={editingComment.text}
            initialPictureUrl={editingComment.pictureUrl || ""}
            isEditing
            onSubmit={handleEditComment}
            onCancelEdit={() => setEditingComment(null)}
          />
        ) : (
          <CommentForm
            bugStatus={bug.status}
            onSubmit={handleCreateComment}
          />
        )}
      </div>

      {/* Edit Bug Modal */}
      {showEditModal && (
        <EditBugModal
          bug={bug}
          onSave={handleEditBug}
          onClose={() => setShowEditModal(false)}
        />
      )}
    </div>
  );
};

export default BugDetailsPage;
