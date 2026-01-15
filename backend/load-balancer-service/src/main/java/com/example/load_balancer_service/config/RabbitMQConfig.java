package com.example.load_balancer_service.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${loadbalancer.queue.input}")
    private String inputQueueName;

    @Value("${monitoring.replicas.count:2}")
    private int replicaCount;

    @Value("${monitoring.queue.prefix:monitoring_ingest_queue_}")
    private String queuePrefix;

    @Bean
    public Declarables ingestQueues() {
        List<Queue> queues = new ArrayList<>();
        for (int i = 1; i <= replicaCount; i++) {
            queues.add(new Queue(queuePrefix + i, true));
        }
        return new Declarables(queues);
    }


    @Bean
    public Queue inputQueue() {
        return new Queue(inputQueueName, true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}