import React, { useState, useContext, useEffect } from "react";
import axios from "axios";
import BugCard, { type Bug } from "../components/BugCard";
import BugFilter from "../components/BugFilter";
import CreateBugForm from "../components/CreateBugForm";
import { UserContext } from "../App";
import "./BugListPage.css";

interface FilterParams {
  search?: string;
  tag?: string;
  authorId?: number;
  own?: boolean;
}

const BugListPage: React.FC = () => {
  const [bugs, setBugs] = useState<Bug[]>([]);
  const [initialLoading, setInitialLoading] = useState(true);
  const [error, setError] = useState("");
  const [filters, setFilters] = useState<FilterParams>({});
  const currentUser = useContext(UserContext);

  const fetchBugs = (newFilters: FilterParams) => {
    setFilters(newFilters);
    (async () => {
      try {
        const params = new URLSearchParams();
        
        if (currentUser?.id) {
          params.append("userId", currentUser.id.toString());
        }
        if (newFilters.search) {
          params.append("search", newFilters.search);
        }
        if (newFilters.tag) {
          params.append("tag", newFilters.tag);
        }
        if (newFilters.authorId) {
          params.append("authorId", newFilters.authorId.toString());
        }
        if (newFilters.own && currentUser?.id) {
          params.append("authorId", currentUser.id.toString());
        }

        const url = `http://localhost:8080/bugs${params.toString() ? '?' + params.toString() : ''}`;
        const res = await axios.get<Bug[]>(url, {
          withCredentials: true,
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
  }, []);

  const handleVoteChange = () => {
    fetchBugs(filters);
  };

  return (
    <div className="bug-list-page">
      <BugFilter onApply={fetchBugs} />
      
      <div className="bug-list-container">
        <h2 className="bug-list-title">Bug Reports</h2>

        {currentUser && (
          <CreateBugForm
            authorId={currentUser.id}
            onBugCreated={(newBug) =>
              setBugs((prev) => [newBug as unknown as Bug, ...prev])
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
          bugs.map((bug) => <BugCard key={bug.id} bug={bug} onVoteChange={handleVoteChange} />)
        )}
      </div>
    </div>
  );
};

export default BugListPage;
