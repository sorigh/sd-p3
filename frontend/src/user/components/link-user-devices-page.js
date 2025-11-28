import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Button } from 'reactstrap';
import { getUserIdByUsername } from '../api/user-api';
import { getDevices, assignDevice, unassignDevice } from "../../device/api/device-api";

const LinkUserDevicesPage = () => {
    const { username } = useParams();
    const [userId, setUserId] = useState(null);
    const [devices, setDevices] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            const id = await getUserIdByUsername(username);
            setUserId(id);
            const allDevices = await getDevices();
            setDevices(allDevices);
        };
        fetchData();
    }, [username]);

    const handleAssign = async (deviceId) => {
        await assignDevice(deviceId, userId);
        const updated = await getDevices();
        setDevices(updated);
    };

    const handleUnassign = async (deviceId) => {
        await unassignDevice(deviceId);
        const updated = await getDevices();
        setDevices(updated);
    };

    return (
        <div className="container mt-4">
            <h2>Link devices to this account: {username}</h2>
            <table className="table">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Type</th>
                        <th>Location</th>
                        <th>Owner</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    {devices.map((d) => (
                        <tr key={d.id}>
                            <td>{d.name}</td>
                            <td>{d.type}</td>
                            <td>{d.location}</td>
                            <td>{d.ownerId || '—'}</td>
                            <td>
                                {d.ownerId === userId ? (
                                    <Button color="danger" onClick={() => handleUnassign(d.id)}>Unlink</Button>
                                ) : (
                                    <Button color="success" onClick={() => handleAssign(d.id)}>Link</Button>
                                )}
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default LinkUserDevicesPage;
