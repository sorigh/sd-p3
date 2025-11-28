import React from "react";
import Table from "../../commons/table";

const DeviceTable = ({ devices, onEdit, onDelete, onAssign, onUnassign }) => {
    const columns = [
    { Header: "ID", accessor: "id" },
    { Header: "Name", accessor: "name" },
    { Header: "Type", accessor: "type" },
    { Header: "Location", accessor: "location" },
    { Header: "Owner ID", accessor: "ownerId" },
    { Header: "Max Energy Consumption", accessor: "maximumHourlyEnergyConsumption" }
];

const searchFields = [
    { accessor: "name" },
    { accessor: "location" },
    { accessor: "type" }
];

    return (
        <div>
            <h2 className="text-center mb-4">Devices Management</h2>
            <Table
                data={devices}
                columns={columns}
                search={searchFields}
                pageSize={5}
                onEdit={onEdit}
                onDelete={onDelete}
                customActions={[
                    {
                        label: "Assign",
                        action: onAssign,
                        condition: (device) => !device.ownerId
                    },
                    {
                        label: "Unassign",
                        action: onUnassign,
                        condition: (device) => device.ownerId
                    }
                ]}
            />
        </div>
    );
};

export default DeviceTable;