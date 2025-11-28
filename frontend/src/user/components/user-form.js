import React, { useState, useEffect } from 'react';
import { Button, Form, FormGroup, Label, Input } from 'reactstrap';

const UserForm = ({ user, onSubmit, isEdit }) => {
    const [formData, setFormData] = useState({
        username: '',
        name: '',
        email: '',
        password: '',
        role: 'USER'
    });

    useEffect(() => {
    if (user) {
        setFormData({
            username: user.username || '',
            name: user.name || '',      // include name
            email: user.email || '',
            role: user.role || 'USER',
            password: '' 
        });
    }
}, [user]);


    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        onSubmit(formData);
    };

    return (
        <Form onSubmit={handleSubmit}>
            <FormGroup>
                <Label for="username">Username</Label>
                <Input
                    type="text"
                    name="username"
                    id="username"
                    value={formData.username}
                    onChange={handleChange}
                    required
                />
            </FormGroup>
            <FormGroup>
                <Label for="name">Name</Label>
                <Input
                    type="text"
                    name="name"
                    id="name"
                    value={formData.name}
                    onChange={handleChange}
                    required
                />
            </FormGroup>
            <FormGroup>
                <Label for="email">Email</Label>
                <Input
                    type="email"
                    name="email"
                    id="email"
                    value={formData.email}
                    onChange={handleChange}
                    required
                />
            </FormGroup>
            {!isEdit && (
                <FormGroup>
                    <Label for="password">Password</Label>
                    <Input
                        type="password"
                        name="password"
                        id="password"
                        value={formData.password}
                        onChange={handleChange}
                        required
                    />
                </FormGroup>
            )}
            <FormGroup>
                <Label for="role">Role</Label>
                <Input
                    type="select"
                    name="role"
                    id="role"
                    value={formData.role}
                    onChange={handleChange}
                >
                    <option value="USER">User</option>
                    <option value="ADMIN">Admin</option>
                </Input>
            </FormGroup>
            <Button color="primary" type="submit">
                {isEdit ? 'Update' : 'Create'} User
            </Button>
        </Form>
    );
};

export default UserForm;