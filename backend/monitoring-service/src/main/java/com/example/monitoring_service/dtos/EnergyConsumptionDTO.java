package com.example.monitoring_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnergyConsumptionDTO {
    private Long deviceId;
    private String timestamp; // Formatted as HH:00 (e.g., "15:00")
    private Double consumptionKwh;
}