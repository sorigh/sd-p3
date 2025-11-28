import React, { useContext } from "react";
import { Navigate } from "react-router-dom";
import { AuthContext } from "../context/authContext";

const ProtectedRoute = ({ children, requiredRoles }) => {
  const { user } = useContext(AuthContext);

  // If not logged in, redirect to login
  if (!user) return <Navigate to="/login" replace />;

  // If roles are defined, check if user has at least one
  if (requiredRoles && !requiredRoles.includes(user.role)) {
    return <Navigate to="/home" replace />;
  }

  return children;
};

export default ProtectedRoute;
