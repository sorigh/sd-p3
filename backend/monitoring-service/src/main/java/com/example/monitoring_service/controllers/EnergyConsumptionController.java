package com.example.monitoring_service.controllers;

import com.example.monitoring_service.dtos.EnergyConsumptionDTO;
import com.example.monitoring_service.service.EnergyConsumptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/monitoring")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class EnergyConsumptionController {

    private final EnergyConsumptionService service;

    /**
     * Endpoint pentru a obține consumul orar al unui dispozitiv pe o zi specifică.
     * Ex: GET /api/monitoring/consumption/1?date=2025-11-24
     */
    @GetMapping("/consumption/{deviceId}")
    public ResponseEntity<List<EnergyConsumptionDTO>> getDeviceConsumption(
            @PathVariable Long deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<EnergyConsumptionDTO> consumption = service.getHourlyConsumptionForDay(deviceId, date);
        return ResponseEntity.ok(consumption);
    }
}