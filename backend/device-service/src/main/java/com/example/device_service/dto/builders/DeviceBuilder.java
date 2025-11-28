package com.example.device_service.dto.builders;

import com.example.device_service.dto.DeviceDTO;
import com.example.device_service.dto.DeviceDetailsDTO;
import com.example.device_service.entity.Device;

public class DeviceBuilder {

    public static DeviceDTO toDeviceDTO(Device device) {
        if (device == null)
            return null;
        // ownerId este setat la null; add in service layer if needed
        return new DeviceDTO(device.getId(), device.getName(), device.getType(),
                device.getLocation(), null, device.getMaximumHourlyEnergyConsumption());
    }

    public static DeviceDetailsDTO toDeviceDetailsDTO(Device device) {
        if (device == null)
            return null;
        return new DeviceDetailsDTO(device.getId(), device.getName(), device.getType(),
                device.getLocation(), null, device.getMaximumHourlyEnergyConsumption());
    }

    public static Device fromDetailsDTO(DeviceDetailsDTO dto) {
        if (dto == null)
            return null;
        Device device = new Device();
        if (dto.getId() != null)
            device.setId(dto.getId());
        device.setName(dto.getName());
        device.setType(dto.getType());
        device.setMaximumHourlyEnergyConsumption(dto.getMaximumHourlyEnergyConsumption());
        device.setLocation(dto.getLocation());
        return device;
    }
}