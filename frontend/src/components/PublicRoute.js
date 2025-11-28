import React, { useContext } from "react";
import { Navigate } from "react-router-dom";
import { AuthContext } from "../context/authContext";

const PublicRoute = ({ children }) => {
  const { user } = useContext(AuthContext);

  // If logged in, redirect to home
  if (user) return <Navigate to="/home" replace />;

  return children;
};

export default PublicRoute;
