package com.example.monitoring_service.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "monitored_users")
@Data
@NoArgsConstructor
public class MonitoredUser {

    @Id
    // ID from Auth/User Service
    private Long id; 

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "role", nullable = false)
    private String role;
}