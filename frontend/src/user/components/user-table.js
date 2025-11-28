import React from "react";
import Table from "../../commons/table";
import { useNavigate } from "react-router-dom";

const UserTable = ({ users, onEdit, onDelete }) => {
    const navigate = useNavigate();

    const columns = [
        {
            Header: "ID",
            accessor: "id",
        },
        {
            Header: "Username",
            accessor: "username",
        },
        {
            Header: "Name",
            accessor: "name",
        },
        {
            Header: "Email",
            accessor: "email",
        },
        {
            Header: "Role",
            accessor: "role",
        },
    ];

    const searchFields = [
        { accessor: "username" },
        { accessor: "email" },
        { accessor: "role" },
    ];

    const handleManageDevices = (user) => {
        navigate(`/users/${user.username}/devices`);
    };

    const handleViewDevices = (user) => {
        navigate(`/users/${user.username}/view-devices`); // new route you need to create
    };


    return (
        <div>
            <h2 className="text-center mb-4">Users Management</h2>
            <Table
                data={users}
                columns={columns}
                search={searchFields}
                pageSize={5}
                onEdit={onEdit}
                onDelete={onDelete}
                extraActions={[
                { label: "Manage Devices", onClick: handleManageDevices, variant: "info" },
                { label: "View Devices", onClick: handleViewDevices, variant: "secondary" },
                ]}
            />
        </div>
    );
};

export default UserTable;
