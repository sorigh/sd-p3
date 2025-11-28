package com.example.device_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;



@Data
public class DeviceDetailsDTO {

    private Long id;

    @NotBlank(message = "name is required")
    private String name;

    private String type;

    private String location;

    private Long ownerId;
    private Double maximumHourlyEnergyConsumption;

    public DeviceDetailsDTO() {
    }

    public DeviceDetailsDTO(Long id, String name, String type, String location, Long ownerId, Double maximumHourlyEnergyConsumption) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.location = location;
        this.ownerId = ownerId;
        this.maximumHourlyEnergyConsumption = maximumHourlyEnergyConsumption;
    }

}