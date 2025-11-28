package com.example.device_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    public static final String SYNC_EXCHANGE = "sync_exchange";
    public static final String USER_CREATED_KEY = "user.created";
    public static final String USER_DELETED_KEY = "user.deleted";

    // Cozi specifice Device Service pentru a consuma evenimentele
    public static final String DEVICE_USER_CREATED_QUEUE = "device_user_created_queue";
    public static final String DEVICE_USER_DELETED_QUEUE = "device_user_deleted_queue";

    @Bean
    public DirectExchange syncExchange() {
        return new DirectExchange(SYNC_EXCHANGE);
    }

    @Bean
    public Queue userCreatedQueue() {
        return new Queue(DEVICE_USER_CREATED_QUEUE, true);
    }

    @Bean
    public Queue userDeletedQueue() {
        return new Queue(DEVICE_USER_DELETED_QUEUE, true);
    }
    
    @Bean
    public Binding userCreatedBinding(Queue userCreatedQueue, DirectExchange syncExchange) {
        return BindingBuilder.bind(userCreatedQueue)
                .to(syncExchange)
                .with(USER_CREATED_KEY);
    }

    @Bean
    public Binding userDeletedBinding(Queue userDeletedQueue, DirectExchange syncExchange) {
        return BindingBuilder.bind(userDeletedQueue)
                .to(syncExchange)
                .with(USER_DELETED_KEY);
    }
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}