import React from "react";
import { Button, FormGroup, Input, Label } from "reactstrap";

class LoginForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = { username: "", password: "" };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(e) {
        this.setState({ [e.target.name]: e.target.value });
    }

    handleSubmit() {
        this.props.onLogin(this.state.username, this.state.password);
    }

    render() {
        return (
            <div>
                <h3>Login</h3>
                <FormGroup>
                    <Label>Username:</Label>
                    <Input type="username" name="username" value={this.state.username} onChange={this.handleChange} />
                </FormGroup>
                <FormGroup>
                    <Label>Password:</Label>
                    <Input type="password" name="password" value={this.state.password} onChange={this.handleChange} />
                </FormGroup>
                <Button color="primary" onClick={this.handleSubmit}>Login</Button>
                <Button color="link" onClick={this.props.switchToRegister}>Register</Button>
            </div>
        );
    }
}

export default LoginForm;
