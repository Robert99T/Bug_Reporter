import React, { useState } from "react";
import { X } from "lucide-react";
import { createBug } from "../api/bugApi";
import type { BugResponse } from "../types";
import "./CreateBugForm.css";

interface CreateBugFormProps {
  authorId: number;
  onBugCreated: (bug: BugResponse) => void;
}

const CreateBugForm: React.FC<CreateBugFormProps> = ({
  authorId,
  onBugCreated,
}) => {
  const [title, setTitle] = useState("");
  const [text, setText] = useState("");
  const [pictureUrl, setPictureUrl] = useState("");
  const [status, setStatus] = useState<"OPEN" | "IN_PROGRESS" | "SOLVED">(
    "OPEN"
  );
  const [tagInput, setTagInput] = useState("");
  const [tags, setTags] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleAddTag = () => {
    const trimmed = tagInput.trim().toLowerCase();
    if (trimmed && !tags.includes(trimmed)) {
      setTags((prev) => [...prev, trimmed]);
    }
    setTagInput("");
  };

  const handleTagKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter" || e.key === ",") {
      e.preventDefault();
      handleAddTag();
    }
  };

  const removeTag = (tag: string) => {
    setTags((prev) => prev.filter((t) => t !== tag));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const response = await createBug({
        title,
        text,
        pictureUrl: pictureUrl || undefined,
        status,
        authorId,
        tags,
      });

      onBugCreated(response.data);

      setTitle("");
      setText("");
      setPictureUrl("");
      setStatus("OPEN");
      setTags([]);
      setTagInput("");
    } catch (err: any) {
      console.error("CREATE BUG ERROR:", err);
      setError(err?.response?.data?.message || "Failed to create bug.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="create-bug-card">
      <h3 className="create-bug-title">Create a New Bug</h3>

      <form className="create-bug-form" onSubmit={handleSubmit}>
        <div className="create-bug-group">
          <label className="create-bug-label">Title</label>
          <input
            className="create-bug-input"
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
          />
        </div>

        <div className="create-bug-group">
          <label className="create-bug-label">Description</label>
          <textarea
            className="create-bug-textarea"
            value={text}
            onChange={(e) => setText(e.target.value)}
            required
          />
        </div>

        <div className="create-bug-group">
          <label className="create-bug-label">Image URL</label>
          <input
            className="create-bug-input"
            type="text"
            value={pictureUrl}
            onChange={(e) => setPictureUrl(e.target.value)}
            placeholder="https://..."
          />
        </div>

        <div className="create-bug-group">
          <label className="create-bug-label">Tags</label>
          <div className="create-bug-tags-wrapper">
            {tags.map((tag) => (
              <span key={tag} className="create-bug-tag-chip">
                #{tag}
                <button
                  type="button"
                  className="create-bug-tag-remove"
                  onClick={() => removeTag(tag)}
                  aria-label={`Remove tag ${tag}`}
                >
                  <X size={12} />
                </button>
              </span>
            ))}
            <input
              className="create-bug-tag-input"
              type="text"
              value={tagInput}
              onChange={(e) => setTagInput(e.target.value)}
              onKeyDown={handleTagKeyDown}
              onBlur={handleAddTag}
              placeholder={tags.length === 0 ? "Type a tag and press Enter..." : ""}
            />
          </div>
        </div>

        <div className="create-bug-group">
          <label className="create-bug-label">Status</label>
          <select
            className="create-bug-select"
            value={status}
            onChange={(e) =>
              setStatus(
                e.target.value as "OPEN" | "IN_PROGRESS" | "SOLVED"
              )
            }
          >
            <option value="OPEN">OPEN</option>
            <option value="IN_PROGRESS">IN_PROGRESS</option>
            <option value="SOLVED">SOLVED</option>
          </select>
        </div>

        {error && <p className="create-bug-error">{error}</p>}

        <div className="create-bug-actions">
          <button className="create-bug-button" type="submit" disabled={loading}>
            {loading ? "Creating..." : "Create Bug"}
          </button>
        </div>
      </form>
    </div>
  );
};

export default CreateBugForm;