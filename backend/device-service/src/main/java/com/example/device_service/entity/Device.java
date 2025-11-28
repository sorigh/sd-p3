package com.example.device_service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "location", nullable = false)
    private String location;
    
    @Column(name = "maximum_hourly_energy_consumption")
    private Double maximumHourlyEnergyConsumption;

    private Instant createdAt;
    private Instant updatedAt;

    public Device() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
}
