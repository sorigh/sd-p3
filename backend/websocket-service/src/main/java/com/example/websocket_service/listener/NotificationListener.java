package com.example.websocket_service.listener;

import com.example.websocket_service.dtos.NotificationDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationListener.class);
    private final SimpMessagingTemplate messagingTemplate; // Unealta de trimitere WebSocket

    // Folosim numele cozii definit în Monitoring Service
    @RabbitListener(queues = "monitoring_notification_queue") 
    public void handleNotification(NotificationDTO notification) {
        LOGGER.info("Received notification for device {}: {}", notification.getDeviceId(), notification.getMessage());

        // AICI trimitem mesajul către toți cei abonați la "/topic/alerts"
        // În viața reală ai putea trimite la "/topic/user/{userId}", dar "/topic/alerts" e suficient pt demo.
        messagingTemplate.convertAndSend("/topic/alerts", notification);
    }
}