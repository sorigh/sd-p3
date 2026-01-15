package com.example.load_balancer_service.consumers;

import com.example.load_balancer_service.dto.MeasurementDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeviceDataDispatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceDataDispatcher.class);
    private final RabbitTemplate rabbitTemplate;

    @Value("${monitoring.replicas.count:2}")
    private int replicaCount;

    @Value("${monitoring.queue.prefix:monitoring_ingest_queue_}")
    private String queuePrefix;

    @RabbitListener(queues = "${loadbalancer.queue.input}")
    public void dispatch(MeasurementDTO measurement) {
        System.out.println("📥 LOAD BALANCER RECEIVED: Device " + measurement.getDevice_id() +
                " value: " + measurement.getMeasurement_value());

        try {
            // Requirement 1.2: Replica Selection Logic (Consistent-style hashing)
            // We use the absolute value of the hash to ensure a positive index
            // Add +1 to the index to match Docker Swarm's 1-based slot IDs
            int replicaIndex = (Math.abs(measurement.getDevice_id().hashCode()) % replicaCount) + 1;
            
            // Generate the target queue name (e.g., monitoring_ingest_queue_0)
            String targetQueue = queuePrefix + replicaIndex;

            LOGGER.info("Dispatching Device {} data to replica queue: {}", 
                    measurement.getDevice_id(), targetQueue);

            // Forward the message to the dedicated ingest queue
            rabbitTemplate.convertAndSend(targetQueue, measurement);

        } catch (Exception e) {
            LOGGER.error("Failed to dispatch measurement: {}", measurement, e);
        }
    }
}