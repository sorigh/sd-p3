package com.example.monitoring_service.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "hourly_consumption")
@Data
@NoArgsConstructor
public class EnergyConsumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false)
    private Long deviceId;

    // The timestamp for the end of the hour
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    // The aggregated energy value (kWh) for that hour
    @Column(name = "consumption_kwh", nullable = false)
    private Double consumptionKwh;
}