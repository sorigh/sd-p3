import React, { useContext } from "react";
import { Navbar, Nav, NavItem, NavLink, Button } from "reactstrap";
import { useNavigate, useLocation } from "react-router-dom";
import { AuthContext } from "../context/authContext"; // adjust path if needed

const AppNavbar = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, setUser } = useContext(AuthContext); // get user from context
  // const username = user?.sub;

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    setUser(null);
    navigate("/");
  };

  if (!user) return null;

  return (
    <Navbar color="light" light className="px-4 shadow-sm d-flex justify-content-between">
      <span
        onClick={() => navigate("/")}
        style={{ cursor: "pointer", fontWeight: "bold", fontSize: "1.2rem" }}
      >
        Energy Dashboard
      </span>

      <Nav navbar className="d-flex flex-row align-items-center gap-3">
        {user.role === "ROLE_ADMIN" && (
          <>
            <NavItem>
              <NavLink
                href="#"
                onClick={() => navigate("/home")}
                active={location.pathname === "/home"}
              >
                Home
              </NavLink>
            </NavItem>

            <NavItem>
              <NavLink
                href="#"
                onClick={() => navigate("/users")}
                active={location.pathname === "/users"}
              >
                Users
              </NavLink>
            </NavItem>

            <NavItem>
              <NavLink
                href="#"
                onClick={() => navigate("/devices")}
                active={location.pathname === "/devices"}
              >
                Devices
              </NavLink>
            </NavItem>
          </>
        )}

        {user.role === "ROLE_USER" && (
          <NavItem>
            <NavLink
              href="#"
              onClick={() => navigate(`/my-devices`)}
              active={location.pathname === `/my-devices`}
            >
              My Devices
            </NavLink>
          </NavItem>
        )}

        <NavItem>
          <Button color="danger" size="sm" onClick={handleLogout}>
            Logout
          </Button>
        </NavItem>
      </Nav>
    </Navbar>
  );
};

export default AppNavbar;
