package com.example.device_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor


@Table(name = "device_ownership", uniqueConstraints = {
        @UniqueConstraint(columnNames = "device_id") 
})
public class DeviceOwnership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false)
    private Long deviceId;

    @Column(name = "user_id", nullable = false)
    private Long userId; // ID-ul utilizatorului din user-service
}