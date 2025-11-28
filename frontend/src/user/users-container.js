import React, { useState, useEffect } from 'react';
import { Button, Modal, ModalHeader, ModalBody } from 'reactstrap';
import UserTable from './components/user-table';
import UserForm from './components/user-form';
import { getUsers, createUser, updateUser, deleteUser } from './api/user-api';
import { registerUser } from '../auth/api/auth-api';
import APIResponseErrorMessage from '../commons/errorhandling/api-response-error-message';

const UsersContainer = () => {
    const [users, setUsers] = useState([]);
    const [error, setError] = useState(null);
    const [modal, setModal] = useState(false);
    const [selectedUser, setSelectedUser] = useState(null);
    const [isEdit, setIsEdit] = useState(false);

    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        try {
            const data = await getUsers();
            console.log("Fetched users:", data); 
            setUsers(data);
            setError(null);
        } catch (err) {
            console.error("Fetch error:", err);
            setError(err.message);
        }
    };

    const handleCreateUser = async (userData) => {
        try {
            // Step 1: Register user in auth-service (auth_db)
            await new Promise((resolve, reject) => {
                registerUser(
                    {
                        username: userData.username,
                        email: userData.email,
                        password: userData.password,
                        userRole: userData.role // Map 'role' to 'userRole' for auth API
                    },
                    (result, status, err) => {
                        if (result && (status === 200 || status === 201)) {
                            console.log("✅ User registered in auth-service:", result);
                            resolve(result);
                        } else {
                            console.error("❌ Auth registration failed:", err);
                            reject(new Error(err?.message || "Failed to register user in auth service"));
                        }
                    }
                );
            });

            // Step 2: Create user in user-service (user_db)
            await createUser(userData);
            
            console.log("✅ User created successfully in both databases");
            setModal(false);
            fetchUsers();
        } catch (err) {
            console.error("❌ User creation error:", err);
            setError(err.message || "Failed to create user");
        }
    };

    const handleEditUser = async (userData) => {
        try {
            await updateUser(selectedUser.id, userData);
            setModal(false);
            fetchUsers();
        } catch (err) {
            setError(err.message);
        }
    };

    const handleDeleteUser = async (user) => {
        if (window.confirm('Are you sure you want to delete this user?')) {
            try {
                await deleteUser(user.id);
                fetchUsers();
                // Note: You might want to also delete from auth_db, 
                // but there's no endpoint for that currently
                console.warn("⚠️ User deleted from user_db only. Consider adding auth cleanup.");
            } catch (err) {
                setError(err.message);
            }
        }
    };

    const toggleModal = () => {
        setModal(!modal);
        if (!modal) {
            setSelectedUser(null);
            setIsEdit(false);
        }
    };

    const openEditModal = (user) => {
        setSelectedUser(user);
        setIsEdit(true);
        setModal(true);
    };

    return (
        <div className="container mt-4">
            {error && <APIResponseErrorMessage error={error} />}

            <Button color="primary" onClick={toggleModal} className="mb-3">
                Create New User
            </Button>

            <UserTable
                users={users}
                onEdit={openEditModal}
                onDelete={handleDeleteUser}
            />

            <Modal isOpen={modal} toggle={toggleModal}>
                <ModalHeader toggle={toggleModal}>
                    {isEdit ? 'Edit User' : 'Create New User'}
                </ModalHeader>
                <ModalBody>
                    <UserForm
                        user={selectedUser}
                        onSubmit={isEdit ? handleEditUser : handleCreateUser}
                        isEdit={isEdit}
                    />
                </ModalBody>
            </Modal>
        </div>
    );
};

export default UsersContainer;