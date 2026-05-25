import React, { useState, useEffect } from "react";
import BugCard from "../components/BugCard";
import BugFilter from "../components/BugFilter";
import CreateBugForm from "../components/CreateBugForm";
import { useAuth } from "../context/AuthContext";
import { getAllBugs } from "../api/bugApi";
import type { BugResponse } from "../types";
import "./BugListPage.css";

interface FilterParams {
  search?: string;
  tag?: string;
  authorId?: number;
  own?: boolean;
}

const BugListPage: React.FC = () => {
  const [bugs, setBugs] = useState<BugResponse[]>([]);
  const [initialLoading, setInitialLoading] = useState(true);
  const [error, setError] = useState("");
  const { currentUser } = useAuth();

  const fetchBugs = (filters: FilterParams) => {
    (async () => {
      try {
        let authorId = filters.authorId;
        if (filters.own && currentUser?.id) {
          authorId = currentUser.id;
        }

        const res = await getAllBugs({
          userId: currentUser?.id,
          search: filters.search,
          tag: filters.tag,
          authorId,
        });

        setBugs(res.data);
        setError("");
      } catch (err) {
        console.error(err);
        setError("Failed to fetch bugs");
      }
    })();
  };

  useEffect(() => {
    fetchBugs({});
    const timer = setTimeout(() => setInitialLoading(false), 500);
    return () => clearTimeout(timer);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <div className="bug-list-page">
      <BugFilter onApply={fetchBugs} />

      <div className="bug-list-container">
        <h2 className="bug-list-title">Bug Reports</h2>

        {currentUser && (
          <CreateBugForm
            authorId={currentUser.id}
            onBugCreated={(newBug) =>
              setBugs((prev) => [newBug, ...prev])
            }
          />
        )}

        {initialLoading ? (
          <p>Loading bugs...</p>
        ) : error ? (
          <p style={{ color: "red" }}>{error}</p>
        ) : bugs.length === 0 ? (
          <p style={{ textAlign: "center" }}>No bugs found.</p>
        ) : (
          bugs.map((bug) => <BugCard key={bug.id} bug={bug} />)
        )}
      </div>
    </div>
  );
};

export default BugListPage;
