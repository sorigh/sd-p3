package com.example.monitoring_service.repositories;

import com.example.monitoring_service.entities.MonitoredDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitoredDeviceRepository extends JpaRepository<MonitoredDevice, Long> {
}