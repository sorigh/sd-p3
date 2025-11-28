package com.example.device_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users") // Local table in device_db
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private Long id; // Same ID as in User Service

    @Column(name = "username")
    private String username;
}