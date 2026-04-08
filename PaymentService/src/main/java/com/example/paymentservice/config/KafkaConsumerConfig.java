package com.example.paymentservice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Consumer Configuration for Payment Service
 *
 * Purpose: Defines how this service consumes events from Kafka topics
 * - Configures connection to Kafka broker
 * - Sets up JSON deserialization for event objects
 * - Enables error handling for malformed messages
 * - Configures consumer group for scaling
 */
@Configuration
@EnableKafka  // Enables Kafka listener annotations in the application
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;  // Kafka broker address (localhost:9092)

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;  // Consumer group name (payment-service-group)

    /**
     * Consumer Factory - Creates Kafka consumers with proper configuration
     *
     * Why needed: Defines how to connect to Kafka and deserialize messages
     *
     * @return ConsumerFactory configured for JSON message consumption
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> config = new HashMap<>();

        // Kafka broker address
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Consumer group ID - allows multiple instances to share load
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        // Deserializer for message KEY (parcelId, paymentId, etc.)
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // Deserializer for message VALUE (event object)
        // UPDATED: Using ErrorHandlingDeserializer wrapper for resilience
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());

        // Trust all packages for JSON deserialization
        // In production: specify exact packages for security
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        // Use type headers to determine which class to deserialize to
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        // Default type if headers missing
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "java.util.HashMap");

        // Start from earliest offset on first run (won't miss events)
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Disable auto-commit for manual control (better error handling)
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return new DefaultKafkaConsumerFactory<>(config);
    }

    /**
     * Kafka Listener Container Factory
     *
     * Purpose: Creates containers that manage @KafkaListener methods
     * - Handles concurrent processing of messages
     * - Manages consumer lifecycle
     * - Provides error handling capabilities
     *
     * @return Configured factory for Kafka listener containers
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

        // Number of concurrent consumers (threads) - adjust based on partition count
        factory.setConcurrency(3);

        // Acknowledge messages after successful processing
        factory.getContainerProperties().setAckMode(
                org.springframework.kafka.listener.ContainerProperties.AckMode.RECORD
        );

        return factory;
    }
}