import React, { useState, useEffect } from 'react';
import { Button, Form, FormGroup, Label, Input } from 'reactstrap';

const DeviceForm = ({ device, onSubmit, isEdit }) => {
    const [formData, setFormData] = useState({
        name: '',
        type: '',
        location: '',
        maximumHourlyEnergyConsumption: 0
    });

    useEffect(() => {
        if (device) {
            setFormData({
                name: device.name || '',
                type: device.type || '',
                location: device.location || '',
                maximumHourlyEnergyConsumption: device.maximumHourlyEnergyConsumption ?? 0
            });
        }
    }, [device]);

    const handleChange = (e) => {
        const value = e.target.type === 'number'
            ? parseFloat(e.target.value)
            : e.target.value;

        setFormData({
            ...formData,
            [e.target.name]: value
        });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        onSubmit(formData);
    };

    return (
        <Form onSubmit={handleSubmit}>
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
                <Label for="type">Type</Label>
                <Input
                    type="text"
                    name="type"
                    id="type"
                    value={formData.type}
                    onChange={handleChange}
                    required
                />
            </FormGroup>

            <FormGroup>
                <Label for="location">Location</Label>
                <Input
                    type="text"
                    name="location"
                    id="location"
                    value={formData.location}
                    onChange={handleChange}
                    required
                />
            </FormGroup>

            <FormGroup>
                <Label for="maximumHourlyEnergyConsumption">Max Hourly Energy Consumption</Label>
                <Input
                    type="number"
                    name="maximumHourlyEnergyConsumption"
                    id="maximumHourlyEnergyConsumption"
                    value={formData.maximumHourlyEnergyConsumption}
                    onChange={handleChange}
                    required
                    step="0.01"
                    
                />
            </FormGroup>

            <Button color="primary" type="submit">
                {isEdit ? 'Update' : 'Create'} Device
            </Button>
        </Form>
    );
};

export default DeviceForm;
