import React from "react";
import LoginForm from "./components/login-form";
import RegisterForm from "./components/register-form";
import { withRouter } from "../commons/with-router";
import { loginUser, registerUser } from "./api/auth-api";
import APIResponseErrorMessage from "../commons/errorhandling/api-response-error-message";
import { AuthContext } from "../context/authContext";

class AuthContainer extends React.Component {

    static contextType = AuthContext;

    // Initialize state and bind methods
    constructor(props) {
        super(props);
        this.state = {
            errorStatus: 0,
            error: null,
            isLogin: true, // toggle between login/register
        };

        this.handleLogin = this.handleLogin.bind(this);
        this.handleRegister = this.handleRegister.bind(this);
        this.toggleForm = this.toggleForm.bind(this);
    }

    // Handle user login
    handleLogin(username, password) {
        loginUser({ username, password }, (result, status, err) => {
            if (result && status === 200) {
                localStorage.setItem("token", result.token); // save JWT

                try {
                    const base64Url = result.token.split('.')[1]; // JWT payload
                    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
                    const payload = JSON.parse(atob(base64));
                    localStorage.setItem("username", payload.sub);
                    this.context.setUser(payload);
                    console.log("User info from token:", payload);
                } catch (e) {
                    console.warn("Could not decode JWT", e);
                }

                console.log("Login successful!");
                this.props.navigate("/home");
                this.setState({ errorStatus: 0, error: null });
            } else {
                this.setState({ errorStatus: status, error: err });
            }
        });
    }

    // Handle user registration
    handleRegister(userData) {
        registerUser(userData, (result, status, err) => {
            if (result && (status === 200 || status === 201)) {
                alert("Registration successful! You can now log in.");
                this.toggleForm(); // switch to login
                this.setState({ errorStatus: 0, error: null });
            } else {
                this.setState({ errorStatus: status, error: err });
            }
        });
    }

    // Toggle between login and registration forms
    toggleForm() {
        this.setState({ isLogin: !this.state.isLogin, errorStatus: 0, error: null });
    }

    // Render the component
    render() {
        return (
            <div className="auth-page">
                {this.state.isLogin ? (
                    <LoginForm onLogin={this.handleLogin} switchToRegister={this.toggleForm} />
                ) : (
                    <RegisterForm onRegister={this.handleRegister} switchToLogin={this.toggleForm} />
                )}
                {this.state.errorStatus > 0 &&
                    <APIResponseErrorMessage errorStatus={this.state.errorStatus} error={this.state.error} />}
            </div>
        );
    }

}

export default withRouter(AuthContainer);