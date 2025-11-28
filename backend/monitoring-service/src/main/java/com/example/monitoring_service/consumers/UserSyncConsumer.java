package com.example.monitoring_service.consumers;

import com.example.monitoring_service.config.RabbitMQConfig;
import com.example.monitoring_service.dtos.UserSyncDTO;
import com.example.monitoring_service.entities.MonitoredUser;
import com.example.monitoring_service.repositories.MonitoredUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UserSyncConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserSyncConsumer.class);
    private final MonitoredUserRepository repository;

    public UserSyncConsumer(MonitoredUserRepository repository) {
        this.repository = repository;
    }

    /**
     * Ascultă evenimentul de creare a unui utilizator și inserează
     * o înregistrare în baza de date locală monitored_users.
     */
    @RabbitListener(queues = RabbitMQConfig.USER_CREATED_QUEUE)
    public void handleUserCreated(UserSyncDTO event) {
        LOGGER.info("Received User CREATED Sync Event for userId: {}", event.getUserId());

        MonitoredUser newUser = new MonitoredUser();
        newUser.setId(event.getUserId());
        newUser.setUsername(event.getUsername());
        newUser.setRole(event.getRole());

        repository.save(newUser);
        
        LOGGER.info("✅ User synced to Monitoring DB: {} ({})", event.getUserId(), event.getUsername());
    }

    @RabbitListener(queues = RabbitMQConfig.USER_DELETED_QUEUE)
    public void handleUserDeleted(UserSyncDTO event) {
        LOGGER.warn("Received User DELETED Sync Event for userId: {}", event.getUserId());

        if (repository.existsById(event.getUserId())) {
            repository.deleteById(event.getUserId());
            LOGGER.info("✅ User removed from Monitoring DB: {}", event.getUserId());
        } else {
            LOGGER.warn("⚠️ User with ID {} not found in Monitoring DB, skipping delete.", event.getUserId());
        }
    }
}