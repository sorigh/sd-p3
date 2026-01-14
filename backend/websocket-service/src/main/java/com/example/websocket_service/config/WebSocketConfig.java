package com.example.websocket_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Activăm un broker simplu în memorie.
        // Clientul se va abona la url-uri care încep cu "/topic"
        config.enableSimpleBroker("/topic");
        
        // Mesajele de la client către server vor începe cu "/app" (ex: chat)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Acesta este endpoint-ul HTTP unde React se conectează inițial
        // URL final: http://localhost:8084/ws
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Permite conexiuni de oriunde (CORS)
                .withSockJS(); // Activează fallback pentru browsere vechi
    }
}