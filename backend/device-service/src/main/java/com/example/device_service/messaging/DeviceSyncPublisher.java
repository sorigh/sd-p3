package com.example.device_service.messaging;

import com.example.device_service.entity.Device;
import com.example.device_service.messaging.dto.DeviceSyncDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class DeviceSyncPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceSyncPublisher.class);
    // same Exchange as UserSyncPublisher
    public static final String SYNC_EXCHANGE = "sync_exchange"; 
    public static final String DEVICE_CREATED_KEY = "device.created";
    public static final String DEVICE_DELETED_KEY = "device.deleted";

    private final RabbitTemplate rabbitTemplate;

    public DeviceSyncPublisher(RabbitTemplate rabbitTemplate, DirectExchange syncExchange) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishDeviceCreated(Device device) {
        DeviceSyncDTO dto = new DeviceSyncDTO(device.getId(), device.getName(), device.getMaximumHourlyEnergyConsumption());
        rabbitTemplate.convertAndSend(SYNC_EXCHANGE, DEVICE_CREATED_KEY, dto);
        LOGGER.info("Published Device CREATED event for deviceId: {}", device.getId());
    }
    public void publishDeviceDeleted(Long deviceId) {
        // only need the ID for deletion, name can be null
        DeviceSyncDTO dto = new DeviceSyncDTO(deviceId, null, null);
        rabbitTemplate.convertAndSend(SYNC_EXCHANGE, DEVICE_DELETED_KEY, dto);
        LOGGER.info("Published Device DELETED event for deviceId: {}", deviceId);
    }
}