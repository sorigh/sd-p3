import React, { useState, useEffect } from 'react';
import { Button, Modal, ModalHeader, ModalBody } from 'reactstrap';
import DeviceTable from './components/device-table';
import DeviceForm from './components/device-form';
import { getDevices, createDevice, updateDevice, deleteDevice, assignDevice, unassignDevice } from './api/device-api';
import APIResponseErrorMessage from '../commons/errorhandling/api-response-error-message';

const DevicesContainer = () => {
    const [devices, setDevices] = useState([]);
    const [error, setError] = useState(null);
    const [modal, setModal] = useState(false);
    const [selectedDevice, setSelectedDevice] = useState(null);
    const [isEdit, setIsEdit] = useState(false);

    useEffect(() => {
        fetchDevices();
    }, []);

    const fetchDevices = async () => {
        try {
            const data = await getDevices();
            setDevices(data);
            setError(null);
        } catch (err) {
            setError(err.message);
        }
    };

    const handleCreateDevice = async (deviceData) => {
        try {
            await createDevice(deviceData);
            setModal(false);
            fetchDevices();
        } catch (err) {
            setError(err.message);
        }
    };

    const handleEditDevice = async (deviceData) => {
        try {
            await updateDevice(selectedDevice.id, deviceData);
            setModal(false);
            fetchDevices();
        } catch (err) {
            setError(err.message);
        }
    };

    const handleDeleteDevice = async (device) => {
        if (window.confirm('Are you sure you want to delete this device?')) {
            try {
                await deleteDevice(device.id);
                fetchDevices();
            } catch (err) {
                setError(err.message);
            }
        }
    };

    const handleAssignDevice = async (device) => {
        const ownerId = prompt('Enter owner ID:');
        if (ownerId) {
            try {
                await assignDevice(device.id, ownerId);
                fetchDevices();
            } catch (err) {
                setError(err.message);
            }
        }
    };

    const handleUnassignDevice = async (device) => {
        if (window.confirm('Are you sure you want to unassign this device?')) {
            try {
                await unassignDevice(device.id);
                fetchDevices();
            } catch (err) {
                setError(err.message);
            }
        }
    };

    const toggleModal = () => {
        setModal(!modal);
        if (!modal) {
            setSelectedDevice(null);
            setIsEdit(false);
        }
    };

    const openEditModal = (device) => {
        setSelectedDevice(device);
        setIsEdit(true);
        setModal(true);
    };

    return (
        <div className="container mt-4">
            {error && <APIResponseErrorMessage error={error} />}

            <Button color="primary" onClick={toggleModal} className="mb-3">
                Create New Device
            </Button>

            <DeviceTable
                devices={devices}
                onEdit={openEditModal}
                onDelete={handleDeleteDevice}
                onAssign={handleAssignDevice}
                onUnassign={handleUnassignDevice}
            />

            <Modal isOpen={modal} toggle={toggleModal}>
                <ModalHeader toggle={toggleModal}>
                    {isEdit ? 'Edit Device' : 'Create New Device'}
                </ModalHeader>
                <ModalBody>
                    <DeviceForm
                        device={selectedDevice}
                        onSubmit={isEdit ? handleEditDevice : handleCreateDevice}
                        isEdit={isEdit}
                    />
                </ModalBody>
            </Modal>
        </div>
    );
};

export default DevicesContainer;