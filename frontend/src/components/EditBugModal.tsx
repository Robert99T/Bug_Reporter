import React, { useState, useEffect } from "react";
import { X } from "lucide-react";
import type { BugResponse, UpdateBugRequest } from "../types";
import "./EditBugModal.css";

interface EditBugModalProps {
  bug: BugResponse;
  onSave: (data: UpdateBugRequest) => void;
  onClose: () => void;
}

const EditBugModal: React.FC<EditBugModalProps> = ({ bug, onSave, onClose }) => {
  const [title, setTitle] = useState(bug.title);
  const [text, setText] = useState(bug.text);
  const [pictureUrl, setPictureUrl] = useState(bug.pictureUrl || "");
  const [tags, setTags] = useState((bug.tags || []).join(", "));

  // Close on Escape key
  useEffect(() => {
    const handleKey = (e: KeyboardEvent) => {
      if (e.key === "Escape") onClose();
    };
    window.addEventListener("keydown", handleKey);
    return () => window.removeEventListener("keydown", handleKey);
  }, [onClose]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const tagList = tags
      .split(",")
      .map((t) => t.trim())
      .filter(Boolean);
    onSave({
      title: title.trim(),
      text: text.trim(),
      pictureUrl: pictureUrl.trim() || undefined,
      tags: tagList.length > 0 ? tagList : undefined,
    });
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3 className="modal-title">Edit Bug Report</h3>
          <button className="modal-close" onClick={onClose} aria-label="Close">
            <X size={18} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="modal-form">
          <div className="modal-field">
            <label className="modal-label">Title</label>
            <input
              type="text"
              className="modal-input"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              required
            />
          </div>

          <div className="modal-field">
            <label className="modal-label">Description</label>
            <textarea
              className="modal-textarea"
              value={text}
              onChange={(e) => setText(e.target.value)}
              rows={5}
              required
            />
          </div>

          <div className="modal-field">
            <label className="modal-label">Image URL</label>
            <input
              type="url"
              className="modal-input"
              value={pictureUrl}
              onChange={(e) => setPictureUrl(e.target.value)}
              placeholder="https://..."
            />
          </div>

          <div className="modal-field">
            <label className="modal-label">Tags (comma-separated)</label>
            <input
              type="text"
              className="modal-input"
              value={tags}
              onChange={(e) => setTags(e.target.value)}
              placeholder="ui, crash, backend"
            />
          </div>

          <div className="modal-actions">
            <button type="button" className="modal-btn-cancel" onClick={onClose}>
              Cancel
            </button>
            <button type="submit" className="modal-btn-save">
              Save Changes
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default EditBugModal;
