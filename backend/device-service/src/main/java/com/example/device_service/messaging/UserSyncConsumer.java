package com.example.device_service.messaging;

import com.example.device_service.config.RabbitMQConfig;
import com.example.device_service.messaging.dto.UserSyncDTO;
import com.example.device_service.service.DeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UserSyncConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSyncConsumer.class);
    private final DeviceService deviceService;

    public UserSyncConsumer(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /**
     * Ascultă evenimentul de ștergere a unui utilizator și
     * șterge toate asocierile din tabelul device_ownership (sincronizare).
     */
    @RabbitListener(queues = RabbitMQConfig.DEVICE_USER_DELETED_QUEUE)
    public void handleUserDeletedEvent(UserSyncDTO event) {
        LOGGER.warn("Received User DELETED Sync Event for userId: {}", event.getUserId());
        
        // Apelează metoda de sincronizare (ștergerea asocierilor)
        deviceService.unassignAllForUser(event.getUserId());
        
        LOGGER.info("✅ Successfully UNASSIGNED all devices from deleted userId: {}", event.getUserId());
    }

    /**
     * Ascultă evenimentul de creare a unui utilizator.
     * Deoarece Device Service nu duplică atributele utilizatorului,
     * acest eveniment este doar logat, dar coada și binding-ul sunt definite
     * conform cerinței pentru extindere.
     */
     @RabbitListener(queues = RabbitMQConfig.DEVICE_USER_CREATED_QUEUE)
    public void handleUserCreatedEvent(UserSyncDTO event) {
        LOGGER.info("Received User CREATED Sync Event for userId: {}. No database action needed.", event.getUserId());
    }
}