package com.example.monitoring_service.service;

import com.example.monitoring_service.dtos.EnergyConsumptionDTO;
import com.example.monitoring_service.entities.EnergyConsumption;
import com.example.monitoring_service.repositories.EnergyConsumptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnergyConsumptionService {

    private final EnergyConsumptionRepository repository;
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:00");

    public List<EnergyConsumptionDTO> getHourlyConsumptionForDay(Long deviceId, LocalDate date) {
        // Intervalul de căutare: de la începutul zilei selectate până la începutul zilei următoare
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime startOfNextDay = date.plusDays(1).atStartOfDay();

        List<EnergyConsumption> consumptionList = repository.findAllByDeviceIdAndTimestampBetweenOrderByTimestampAsc(
                deviceId,
                startOfDay,
                startOfNextDay
        );

        return consumptionList.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    private EnergyConsumptionDTO toDTO(EnergyConsumption entity) {
        // Timestamp-ul entității este sfârșitul orei (ex: 15:00), perfect pentru axa X.
        return new EnergyConsumptionDTO(
                entity.getDeviceId(),
                entity.getTimestamp().format(HOUR_FORMATTER),
                entity.getConsumptionKwh()
        );
    }
}