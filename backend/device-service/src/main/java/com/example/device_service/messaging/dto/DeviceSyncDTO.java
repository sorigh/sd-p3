package com.example.device_service.messaging.dto;

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
    @JsonProperty("max_hourly_consumption")
    private Double maxHourlyConsumption;
}