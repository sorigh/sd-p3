import React, { useContext } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import AuthContainer from "./auth/auth-container";
import UsersContainer from "./user/users-container";
import DevicesContainer from "./device/devices-container";
import HomePage from "./home/home";
import Navbar from "./components/Navbar";
import { AuthContext } from "./context/authContext";
import LinkUserDevicesPage from "./user/components/link-user-devices-page";
import UserDevicesViewPage from "./user/components/user-devices-view-page";
import MyDevicesPage from "./device/components/my-devices-page";
import "bootstrap/dist/css/bootstrap.min.css";
import DeviceConsumptionPage from "./device/components/device-consumption-page";

function ProtectedRoute({ children, requiredRole }) {
  const { user, loading } = useContext(AuthContext);

  if (loading) return <p>Loading...</p>; // <-- wait until auth is loaded
  if (!user) return <Navigate to="/login" replace />;
  if (requiredRole && user.role !== requiredRole) return <Navigate to="/home" replace />;
  return children;
}

function App() {
  // const token = localStorage.getItem("token");
  const { user } = useContext(AuthContext);
  const isAuthenticated = !!user;

  return (
    <Router>
      {isAuthenticated && <Navbar />}  {/* Navbar appears only when logged in */}

      <Routes>
        <Route path="/" element={isAuthenticated ? <Navigate to="/home" /> : <AuthContainer />} />
        <Route path="/login" element={<AuthContainer />} />
        <Route path="/home" element={<HomePage />} />

        <Route
          path="/users"
          element={
            <ProtectedRoute requiredRole="ROLE_ADMIN">
              <UsersContainer />
            </ProtectedRoute>
          }
        />
        <Route path="/users/:username/devices" element={<LinkUserDevicesPage />} />

        <Route
          path="/devices"
          element={
            <ProtectedRoute>
              <DevicesContainer />
            </ProtectedRoute>
          }
        />

        <Route path="/users/:username/view-devices" element={<UserDevicesViewPage />} />

        <Route path="/my-devices" element={<MyDevicesPage />} />
        <Route 
          path="/my-devices/:deviceId/consumption" 
          element={
            <ProtectedRoute requiredRole="ROLE_USER">
              <DeviceConsumptionPage />
            </ProtectedRoute>
          } 
        />
        

      </Routes>
    </Router>
  );
}

export default App;
