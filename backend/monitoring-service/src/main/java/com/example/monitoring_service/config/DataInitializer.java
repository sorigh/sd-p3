package com.example.monitoring_service.config;

import com.example.monitoring_service.entities.EnergyConsumption;
import com.example.monitoring_service.entities.MonitoredDevice;
import com.example.monitoring_service.repositories.EnergyConsumptionRepository;
import com.example.monitoring_service.repositories.MonitoredDeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final EnergyConsumptionRepository consumptionRepository;
    private final MonitoredDeviceRepository deviceRepository;

    private static final Long TEST_DEVICE_ID = 1L;
    private static final LocalDate TEST_DATE = LocalDate.of(2025, 11, 26);

    @Bean
    public CommandLineRunner initMonitoringData() {
        return args -> {
            // 1. Initialize Monitored Device (Pre-sync simulation)
            if (deviceRepository.findById(TEST_DEVICE_ID).isEmpty()) {
                MonitoredDevice device = new MonitoredDevice();
                device.setId(TEST_DEVICE_ID);
                device.setName("Simulated Device 1");
                deviceRepository.save(device);
                System.out.println("✅ Initialized Monitored Device: ID 1");
            }
            
            // We ensure we only run the data insertion once per application lifecycle
            if (consumptionRepository.findAllByDeviceIdAndTimestampBetweenOrderByTimestampAsc(
                    TEST_DEVICE_ID, TEST_DATE.atStartOfDay(), TEST_DATE.plusDays(1).atStartOfDay()
            ).isEmpty()) {
                
                System.out.println("⏳ Inserting simulated consumption data for " + TEST_DATE);

                // Consumption data (simplified averages from simulator.py logic)
                for (int hour = 0; hour < 24; hour++) {
                    double consumption;
                    // Night (1 AM - 7 AM, reporting timestamps 02:00 - 08:00)
                    if (hour >= 1 && hour <= 7) {
                        consumption = 0.15; 
                    } 
                    // Peak (5 PM - 10 PM, reporting timestamps 18:00 - 23:00)
                    else if (hour >= 17 && hour <= 22) {
                        consumption = 0.54;
                    } 
                    // Day (0, 8-16, 23, reporting timestamps 01:00, 09:00-17:00, 00:00 on next day)
                    else {
                        consumption = 0.30;
                    }

                    // Timestamp for the *end* of the hour (e.g., hour 1 means 01:00:00)
                    LocalDateTime timestamp = TEST_DATE.atTime(LocalTime.of(hour, 0)).plusHours(1);
                    
                    saveConsumptionEntry(TEST_DEVICE_ID, timestamp, consumption);
                }
                
                System.out.println("✅ Finished inserting 24 hourly entries for Device 1 on " + TEST_DATE);
            }
        };
    }

    private void saveConsumptionEntry(Long deviceId, LocalDateTime timestamp, double consumptionKwh) {
        EnergyConsumption entry = new EnergyConsumption();
        entry.setDeviceId(deviceId);
        entry.setTimestamp(timestamp);
        entry.setConsumptionKwh(consumptionKwh);
        consumptionRepository.save(entry);
    }
}