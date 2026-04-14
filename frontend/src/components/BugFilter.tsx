import React, { useState, useEffect, useRef } from "react";
import { Search, Tag as TagIcon, User, UserCircle, X } from "lucide-react";
import "./BugFilter.css";

export interface UserOption {
  id: number;
  username: string;
}

export interface TagOption {
  name: string;
}

interface BugFilterProps {
  onApply: (filters: {
    search?: string;
    tag?: string;
    authorId?: number;
    own?: boolean;
  }) => void;
}

const BugFilter: React.FC<BugFilterProps> = ({ onApply }) => {
  const [search, setSearch] = useState("");
  const [selectedTag, setSelectedTag] = useState("");
  const [selectedAuthorName, setSelectedAuthorName] = useState("");
  const [showOwn, setShowOwn] = useState(false);
  const [tags, setTags] = useState<TagOption[]>([]);
  const [users, setUsers] = useState<UserOption[]>([]);
  
  const onApplyRef = useRef(onApply);
  onApplyRef.current = onApply;

  useEffect(() => {
    const fetchOptions = async () => {
      try {
        const [tagsRes, usersRes] = await Promise.all([
          fetch("http://localhost:8080/api/tags").then((r) => r.json()),
          fetch("http://localhost:8080/api/users").then((r) => r.json()),
        ]);
        setTags(tagsRes);
        setUsers(usersRes);
      } catch (err) {
        console.error("Failed to fetch filter options:", err);
      }
    };
    fetchOptions();
  }, []);

  useEffect(() => {
    const timeoutId = setTimeout(() => {
      let authorId: number | undefined;
      if (selectedAuthorName) {
        const user = users.find((u) => u.username.toLowerCase() === selectedAuthorName.toLowerCase());
        if (user) {
          authorId = user.id;
        }
      }

      onApplyRef.current({
        search: search || undefined,
        tag: selectedTag || undefined,
        authorId,
        own: showOwn,
      });
    }, 400);

    return () => clearTimeout(timeoutId);
  }, [search, selectedTag, selectedAuthorName, showOwn]);

  const handleClear = () => {
    setSearch("");
    setSelectedTag("");
    setSelectedAuthorName("");
    setShowOwn(false);
    onApply({});
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") {
      clearTimeout;
    }
  };

  const hasFilters = search || selectedTag || selectedAuthorName || showOwn;

  return (
    <div className="bug-filter">
      <div className="filter-row">
        <div className="filter-group search-group">
          <Search size={16} className="filter-icon" />
          <input
            type="text"
            placeholder="Search by title..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            onKeyDown={handleKeyDown}
            className="filter-input"
          />
        </div>

        <div className="filter-group">
          <TagIcon size={16} className="filter-icon" />
          <input
            type="text"
            placeholder="Filter by tag..."
            value={selectedTag}
            onChange={(e) => setSelectedTag(e.target.value)}
            list="tags-list"
            className="filter-input"
          />
          <datalist id="tags-list">
            {tags.map((tag) => (
              <option key={tag.name} value={tag.name} />
            ))}
          </datalist>
        </div>

        <div className="filter-group">
          <User size={16} className="filter-icon" />
          <input
            type="text"
            placeholder="Filter by user..."
            value={selectedAuthorName}
            onChange={(e) => {
              setSelectedAuthorName(e.target.value);
              if (e.target.value) setShowOwn(false);
            }}
            list="users-list"
            className="filter-input"
          />
          <datalist id="users-list">
            {users.map((user) => (
              <option key={user.id} value={user.username} />
            ))}
          </datalist>
        </div>

        <div className="filter-group">
          <button
            className={`filter-toggle ${showOwn ? "active" : ""}`}
            onClick={() => {
              setShowOwn(!showOwn);
              if (!showOwn) setSelectedAuthorName("");
            }}
          >
            <UserCircle size={16} />
            My Bugs
          </button>
        </div>

        {hasFilters && (
          <button className="filter-clear" onClick={handleClear}>
            <X size={16} />
            Clear
          </button>
        )}
      </div>
    </div>
  );
};

export default BugFilter;
