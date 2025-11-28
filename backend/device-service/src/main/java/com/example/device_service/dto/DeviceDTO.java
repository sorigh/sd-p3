package com.example.device_service.dto;

import lombok.Data;


@Data
public class DeviceDTO {
    private Long id;
    private String name;
    private String type;
    private String location;
    private Long ownerId;
    private Double maximumHourlyEnergyConsumption;

    public DeviceDTO() {
    }

     public DeviceDTO(Long id, String name, String type, String location, Long ownerId, Double maximumHourlyEnergyConsumption) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.location = location;
        this.ownerId = ownerId;
        this.maximumHourlyEnergyConsumption = maximumHourlyEnergyConsumption;
    }

}