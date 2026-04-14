import React, { useState, useEffect } from "react";
import { Send, ImagePlus } from "lucide-react";
import "./CommentForm.css";

interface CommentFormProps {
  bugStatus: string;
  initialText?: string;
  initialPictureUrl?: string;
  isEditing?: boolean;
  onSubmit: (text: string, pictureUrl?: string) => void;
  onCancelEdit?: () => void;
}

const CommentForm: React.FC<CommentFormProps> = ({
  bugStatus,
  initialText = "",
  initialPictureUrl = "",
  isEditing = false,
  onSubmit,
  onCancelEdit,
}) => {
  const [text, setText] = useState(initialText);
  const [pictureUrl, setPictureUrl] = useState(initialPictureUrl);
  const [showImageInput, setShowImageInput] = useState(!!initialPictureUrl);

  const isSolved = bugStatus === "SOLVED";

  // Reset form when editing state changes
  useEffect(() => {
    setText(initialText);
    setPictureUrl(initialPictureUrl);
    setShowImageInput(!!initialPictureUrl);
  }, [initialText, initialPictureUrl, isEditing]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const trimmed = text.trim();
    if (!trimmed) return;
    onSubmit(trimmed, pictureUrl.trim() || undefined);
    if (!isEditing) {
      setText("");
      setPictureUrl("");
      setShowImageInput(false);
    }
  };

  if (isSolved) {
    return (
      <div className="comment-form-solved">
        <div className="solved-badge">
          <span className="solved-icon">✓</span>
          This bug has been marked as solved — no new comments can be added.
        </div>
      </div>
    );
  }

  return (
    <form className="comment-form" onSubmit={handleSubmit}>
      <div className="comment-form-header">
        <h4 className="comment-form-title">
          {isEditing ? "Edit Comment" : "Add a Comment"}
        </h4>
      </div>

      <textarea
        className="comment-form-textarea"
        placeholder="Write your comment..."
        value={text}
        onChange={(e) => setText(e.target.value)}
        rows={3}
        required
      />

      {showImageInput && (
        <input
          type="url"
          className="comment-form-image-input"
          placeholder="Paste image URL (optional)"
          value={pictureUrl}
          onChange={(e) => setPictureUrl(e.target.value)}
        />
      )}

      <div className="comment-form-footer">
        <button
          type="button"
          className="comment-form-image-toggle"
          onClick={() => setShowImageInput(!showImageInput)}
          title="Add image URL"
        >
          <ImagePlus size={16} />
          {showImageInput ? "Hide image" : "Add image"}
        </button>

        <div className="comment-form-actions">
          {isEditing && onCancelEdit && (
            <button
              type="button"
              className="comment-form-cancel"
              onClick={onCancelEdit}
            >
              Cancel
            </button>
          )}
          <button
            type="submit"
            className="comment-form-submit"
            disabled={!text.trim()}
          >
            <Send size={14} />
            {isEditing ? "Update" : "Post"}
          </button>
        </div>
      </div>
    </form>
  );
};

export default CommentForm;
