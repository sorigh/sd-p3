import React from "react";
import { Button, FormGroup, Input, Label } from "reactstrap";

class RegisterForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = { email: "", password: "", username: "" };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(e) {
        this.setState({ [e.target.name]: e.target.value });
    }

    handleSubmit() {
        this.props.onRegister(this.state);
    }

    render() {
        return (
            <div>
                <h3>Register</h3>
                <FormGroup>
                    <Label>Username:</Label>
                    <Input type="text" name="username" value={this.state.username} onChange={this.handleChange} />
                </FormGroup>
                <FormGroup>
                    <Label>Email:</Label>
                    <Input type="email" name="email" value={this.state.email} onChange={this.handleChange} />
                </FormGroup>
                <FormGroup>
                    <Label>Password:</Label>
                    <Input type="password" name="password" value={this.state.password} onChange={this.handleChange} />
                </FormGroup>
                <Button color="primary" onClick={this.handleSubmit}>Register</Button>
                <Button color="link" onClick={this.props.switchToLogin}>Back to Login</Button>
            </div>
        );
    }
}

export default RegisterForm;
