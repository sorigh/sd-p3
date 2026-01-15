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


// 1. Importăm containerul pentru pop-up-uri (vizual)
import { ToastContainer } from 'react-toastify';
// 2. Importăm stilurile obligatorii pentru pop-up-uri
import 'react-toastify/dist/ReactToastify.css';

// 3. Importăm componenta invizibilă care ascultă WebSocket-ul
import WebSocketNotification from './components/WebSocketNotification';


import ChatInterface from './components/ChatInterface'; // No curly braces for default exports

import AdminChatDashboard from "./components/AdminChatDashboard";

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
      
      <WebSocketNotification />
      
      
      <ToastContainer 
            // popups
            position="top-right"
            autoClose={5000}
            hideProgressBar={false}
            newestOnTop={false}
            closeOnClick
            rtl={false}
            pauseOnFocusLoss
            draggable
            pauseOnHover
            theme="colored"
      />

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
        <Route 
          path="/chat" 
          element={
            <ProtectedRoute>
              <ChatInterface />
            </ProtectedRoute>
          } 
        />
        <Route 
          path="/admin/chat" 
          element={
              <ProtectedRoute>
                    <AdminChatDashboard />
                </ProtectedRoute>
            } 
        />
        

      </Routes>
    </Router>
  );
}

export default App;
