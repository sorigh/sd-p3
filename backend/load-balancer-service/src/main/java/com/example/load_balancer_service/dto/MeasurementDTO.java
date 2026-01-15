package com.example.load_balancer_service.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class MeasurementDTO {
    private String timestamp;
    
    @JsonProperty("consumption")
    private Double measurement_value;
    
    private DeviceData device;

    @Data
    public static class DeviceData {
        private String id;
    }

    public Long getDevice_id() {
        if (device != null && device.getId() != null) {
            return Long.parseLong(device.getId());
        }
        return null; 
    }

    public LocalDateTime getLocalTimestamp() {
        return LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}