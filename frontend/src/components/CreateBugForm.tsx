import React, { useState } from "react";
import { createBug, type BugResponse } from "../api/bugApi";
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
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

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
        tags: [],
      });

      onBugCreated(response.data);

      setTitle("");
      setText("");
      setPictureUrl("");
      setStatus("OPEN");
    } catch (err: any) {
      console.error("CREATE BUG ERROR:", err);
      console.error("STATUS:", err?.response?.status);
      console.error("DATA:", err?.response?.data);
      setError(err?.response?.data?.message || "Nu s-a putut crea bug-ul.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="create-bug-card">
      <h3 className="create-bug-title">Creează un bug nou</h3>

      <form className="create-bug-form" onSubmit={handleSubmit}>
        <div className="create-bug-group">
          <label className="create-bug-label">Titlu</label>
          <input
            className="create-bug-input"
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
          />
        </div>

        <div className="create-bug-group">
          <label className="create-bug-label">Descriere</label>
          <textarea
            className="create-bug-textarea"
            value={text}
            onChange={(e) => setText(e.target.value)}
            required
          />
        </div>

        <div className="create-bug-group">
          <label className="create-bug-label">URL imagine</label>
          <input
            className="create-bug-input"
            type="text"
            value={pictureUrl}
            onChange={(e) => setPictureUrl(e.target.value)}
          />
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
            {loading ? "Se creează..." : "Creează bug"}
          </button>
        </div>
      </form>
    </div>
  );
};

export default CreateBugForm;