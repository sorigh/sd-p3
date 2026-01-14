package com.example.monitoring_service.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSyncDTO {
    private Long deviceId;
    private String name;
    @JsonProperty("maximumHourlyEnergyConsumption")
    private Double maxHourlyConsumption;
}