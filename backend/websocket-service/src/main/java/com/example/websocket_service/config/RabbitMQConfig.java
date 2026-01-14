package com.example.websocket_service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Definim coada explicit.
    // Dacă nu există, RabbitMQ o va crea. Dacă există, o va folosi pe cea existentă.
    @Bean
    public Queue notificationQueue() {
        // "monitoring_notification_queue" trebuie să fie EXACT numele din eroare
        return new Queue("monitoring_notification_queue", true); // true = durable
    }
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        // Spunem converterului să ignore informațiile despre clasă din header-ul RabbitMQ
        // și să încerce să mapeze pe ce îi dăm noi în @RabbitListener
        return converter;
    }
}