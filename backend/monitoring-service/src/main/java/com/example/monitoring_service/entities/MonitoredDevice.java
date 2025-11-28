package com.example.monitoring_service.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "monitored_devices")
@Data
@NoArgsConstructor
public class MonitoredDevice {

    @Id
    // ID from device service (PK)
    private Long id; 

    @Column(name = "name")
    private String name;
}