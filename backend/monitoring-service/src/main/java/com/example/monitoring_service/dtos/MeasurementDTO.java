package com.example.monitoring_service.dtos;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class MeasurementDTO {
    
    private String timestamp;
    
    // 1. Map "consumption" from JSON to this field
    @JsonProperty("consumption")
    private Double measurement_value;
    
    // 2. Handle the nested "device" object
    private DeviceData device;

    @Data
    public static class DeviceData {
        private String id;
    }

    // 3. Keep this helper so your Consumer code doesn't break
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