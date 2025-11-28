import { HOST } from "../../commons/hosts";

export const getUsers = async () => {
    const response = await fetch(`${HOST.user_service}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem("token")}`,
            'Content-Type': 'application/json',
        },
    });
    if (!response.ok) throw new Error('Failed to fetch users');
    return response.json();
};

export const getUserById = async (id) => {
    const response = await fetch(`${HOST.user_service}/${id}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem("token")}`,
            'Content-Type': 'application/json',
        },
    });
    if (!response.ok) throw new Error('Failed to fetch user');
    return response.json();
};

export async function getUserIdByUsername(username) {
    const token = localStorage.getItem("token");
    if (!token) throw new Error("No auth token found");
    const response = await fetch(`${HOST.user_service}/username/${username}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
        },
    });
    
    if (!response.ok) {
        throw new Error(`Failed to fetch user ID: ${response.status} ${response.statusText}`);
    }

    const data = await response.json();  
    console.log("Get user id by username result:", data);
    return data;
}


export const createUser = async (userData) => {
    console.log("Sending user to backend:", userData);
    const response = await fetch(`${HOST.user_service}`, {
        
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem("token")}`,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData),
    });
    if (!response.ok) throw new Error('Failed to create user');
    return response.headers.get("Location");
};

export const updateUser = async (id, userData) => {
    const response = await fetch(`${HOST.user_service}/${id}`, {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem("token")}`,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData),
    });
    if (!response.ok) throw new Error('Failed to update user');
};

export const deleteUser = async (id) => {
    const response = await fetch(`${HOST.user_service}/${id}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem("token")}`,
        },
    });
    if (!response.ok) throw new Error('Failed to delete user');
};