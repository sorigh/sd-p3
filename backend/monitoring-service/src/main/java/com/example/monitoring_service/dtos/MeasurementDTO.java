package com.example.monitoring_service.dtos;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class MeasurementDTO {
    private String timestamp;
    private Long device_id;
    private Double measurement_value;
    
    // Helper to convert the string timestamp into a Java DateTime object
    public LocalDateTime getLocalTimestamp() {
        return LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}