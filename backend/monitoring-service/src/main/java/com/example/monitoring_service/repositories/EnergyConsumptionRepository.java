package com.example.monitoring_service.repositories;

import com.example.monitoring_service.entities.EnergyConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EnergyConsumptionRepository extends JpaRepository<EnergyConsumption, Long> {
    
    // Metodă adăugată pentru a filtra după deviceId și interval orar (o zi)
    List<EnergyConsumption> findAllByDeviceIdAndTimestampBetweenOrderByTimestampAsc(
            Long deviceId,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime);
}