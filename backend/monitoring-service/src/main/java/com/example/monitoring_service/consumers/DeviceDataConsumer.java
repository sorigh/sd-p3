package com.example.monitoring_service.consumers;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.example.monitoring_service.dtos.MeasurementDTO;
import com.example.monitoring_service.dtos.NotificationDTO;
import com.example.monitoring_service.entities.EnergyConsumption;
import com.example.monitoring_service.entities.MonitoredDevice;
import com.example.monitoring_service.repositories.EnergyConsumptionRepository;
import com.example.monitoring_service.repositories.MonitoredDeviceRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DeviceDataConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceDataConsumer.class);
    private final EnergyConsumptionRepository repository;
    private final MonitoredDeviceRepository monitoredDeviceRepository;
    private final RabbitTemplate rabbitTemplate; // new for sending notifications

    // Key: "deviceId_yyyy-MM-dd-HH" (e.g., "1_2025-11-22-19")
    // Value: Current accumulated consumption for that hour
    private final Map<String, Double> hourlyAccumulator = new ConcurrentHashMap<>();

    private static final String NOTIFICATION_QUEUE = "monitoring_notification_queue";


    // The primary message consumer, listening to the data queue
    @RabbitListener(queues = "${monitoring.queue.data}")
    @RabbitListener(queues = "${monitoring.queue.data}")
    public void handleDeviceData(MeasurementDTO measurement) {
        try {
            Long deviceId = measurement.getDevice_id();
            Double value = measurement.getMeasurement_value();
            LocalDateTime dateTime = measurement.getLocalTimestamp();
            
            LOGGER.info("Received reading for Device {}: {} kWh", deviceId, value);

            // --- FORȚĂM O ALERTĂ PENTRU TEST ---
            if (value > 10.0) { // Punem o limită mică, hardcodată
                LOGGER.warn("⚠️ FORCING ALERT FOR TESTING!");
                sendOverconsumptionNotification(deviceId, value, 10.0);
            }
            // -------------------------------------

            // Restul logicii tale cu hourlyAccumulator...
        } catch (Exception e) {
            LOGGER.error("Error: ", e);
        }
    }
    
    // Saves the final hourly total to the database
    private void saveHourlyConsumption(String hourKey, Double totalConsumption, LocalDateTime timeOfLastReading) {
        
        // Folosim remove(key, value) atomic pentru a ne asigura că ștergem intrarea doar dacă
        // valoarea este exact cea pe care tocmai am calculat-o, prevenind scrierea dublă.

        boolean wasRemoved = hourlyAccumulator.remove(hourKey, totalConsumption);

        if (wasRemoved) {
            // Doar daca a fost șters cu succes, salvam în baza de date.
            
            // Find the start of the next hour (the reporting timestamp)
            LocalDateTime endOfHour = timeOfLastReading.toLocalDate().atTime(timeOfLastReading.getHour(), 0).plusHours(1);

            EnergyConsumption hourlyEntry = new EnergyConsumption();
            hourlyEntry.setDeviceId(Long.parseLong(hourKey.split("_")[0]));
            hourlyEntry.setTimestamp(endOfHour);
            hourlyEntry.setConsumptionKwh(totalConsumption);

            repository.save(hourlyEntry);
            LOGGER.info("✅ SAVED HOURLY AGGREGATION for Device {} ({} kWh)", hourlyEntry.getDeviceId(), totalConsumption);
        } else {
             // Aceasta ramura ar putea insemna ca datele au fost deja sterse sau modificate (un alt thread a preluat controlul).
             LOGGER.warn("Skipping save for {} ({} kWh). Accumulator value mismatch or entry already removed.", hourKey, totalConsumption);
        }
    }

    private void sendOverconsumptionNotification(Long deviceId, Double currentConsumption, Double maxConsumption) {
        // Poți crea un DTO rapid sau folosi un Map/String JSON
        String message = String.format("Device %d exceeded limit! Current: %.2f, Max: %.2f", 
                                       deviceId, currentConsumption, maxConsumption);
        
        // Trimitem un obiect simplu (sau un DTO dedicat NotificationDTO)
        // Recomandat: creează un NotificationDTO { message, deviceId, timestamp }
        NotificationDTO notification = new NotificationDTO(deviceId, message, LocalDateTime.now());

        // Trimite pe coada pe care o va asculta WebSocket Service
        rabbitTemplate.convertAndSend(NOTIFICATION_QUEUE, notification);
        LOGGER.info("Notification sent to queue: {}", message);
    }

}
