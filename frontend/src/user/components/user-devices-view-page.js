import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import Table from "../../commons/table";
import { getUserIdByUsername } from "../../user/api/user-api";
import { getDevices } from "../../device/api/device-api";

const UserDevicesViewPage = () => {
  const { username } = useParams();
  const [user, setUser] = useState(null);
  const [devices, setDevices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchDevices = async () => {
      try {
        // Get user ID
        const userData = await getUserIdByUsername(username);
        console.log("User data from API:", userData); 
        setUser(userData);

        // Get all devices
        const allDevices = await getDevices();
        console.log("All devices from API:", allDevices);

        // Filter devices assigned to this user
        const assignedDevices = allDevices.filter(
        (device) => device.ownerId === userData //userData is the id
        );
        console.log("Filtered assigned devices:", assignedDevices); 

        setDevices(assignedDevices);
        setLoading(false);
      } catch (err) {
        console.error(err);
        setError(err.message || "Failed to load devices");
        setLoading(false);
      }
    };

    fetchDevices();
  }, [username]);

   const columns = [
    { Header: "ID", accessor: "id" },
    { Header: "Name", accessor: "name" },
    { Header: "Type", accessor: "type" },
    { Header: "Location", accessor: "location" },
    { 
      Header: "Max Hourly Energy", 
      accessor: "maximumHourlyEnergyConsumption" 
    }
  ];

  if (loading) return <p>Loading devices...</p>;
  if (error) return <p className="text-danger">{error}</p>;

  return (
    <div>
      <h2 className="text-center mb-4">
        Devices Assigned to {user?.username || username}
      </h2>
      <Table data={devices} columns={columns} pageSize={5} />
    </div>
  );
};

export default UserDevicesViewPage;
