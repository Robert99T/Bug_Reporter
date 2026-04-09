import React, { useState } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Login from "./Login";
import BugForm from "./BugList.tsx";

const App: React.FC = () => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    return (
        <Router>
            <Routes>
                {/* Login page */}
                <Route
                    path="/login"
                    element={<Login onLogin={() => setIsAuthenticated(true)} />}
                />

                {/* Home page now shows all bugs */}
                <Route
                    path="/"
                    element={
                        isAuthenticated ? <BugForm /> : <Navigate to="/login" replace />
                    }
                />

                {/* Optional: explicit /bugs route */}
                <Route
                    path="/bugs"
                    element={
                        isAuthenticated ? <BugForm /> : <Navigate to="/login" replace />
                    }
                />
            </Routes>
        </Router>
    );
};

export default App;