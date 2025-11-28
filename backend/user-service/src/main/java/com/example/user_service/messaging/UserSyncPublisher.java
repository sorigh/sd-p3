package com.example.user_service.messaging;

import com.example.user_service.entity.User;
import com.example.user_service.messaging.dto.UserSyncDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserSyncPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSyncPublisher.class);

    public static final String SYNC_EXCHANGE = "sync_exchange";
    public static final String USER_CREATED_KEY = "user.created";
    public static final String USER_DELETED_KEY = "user.deleted";

    private final RabbitTemplate rabbitTemplate;

    public UserSyncPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishUserCreated(User user) {
        String roleString = user.getRole().name();
        UserSyncDTO dto = new UserSyncDTO(user.getId(), user.getUsername(), roleString);
        rabbitTemplate.convertAndSend(SYNC_EXCHANGE, USER_CREATED_KEY, dto);
        LOGGER.info("Published User CREATED event for userId: {}", user.getId());
    }

    public void publishUserDeleted(Long userId) {
        UserSyncDTO dto = new UserSyncDTO(userId, null, null);
        rabbitTemplate.convertAndSend(SYNC_EXCHANGE, USER_DELETED_KEY, dto);
        LOGGER.info("Published User DELETED event for userId: {}", userId);
    }
}