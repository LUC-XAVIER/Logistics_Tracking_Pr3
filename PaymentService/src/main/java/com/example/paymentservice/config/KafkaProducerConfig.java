package com.example.paymentservice.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Producer Configuration for Parcel Service
 *
 * Purpose: Defines how this service publishes events to Kafka topics
 * - Configures connection to Kafka broker
 * - Sets up JSON serialization for event objects
 * - Ensures reliable message delivery
 */
@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;  // Kafka broker address

    /**
     * Producer Factory - Creates Kafka producers with proper configuration
     *
     * Why needed: Defines how to connect to Kafka and serialize messages
     *
     * @return ProducerFactory configured for JSON message production
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();

        // Kafka broker address
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Serializer for message KEY (parcelId, userId, etc.)
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // Serializer for message VALUE (event object)
        // UPDATED: Using org.springframework.kafka.support.serializer (NOT deprecated)
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Reliability settings
        // acks=all: Wait for all replicas to acknowledge (strongest guarantee)
        config.put(ProducerConfig.ACKS_CONFIG, "all");

        // Enable idempotence: Prevents duplicate messages on retry
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        // Retry failed sends up to 3 times
        config.put(ProducerConfig.RETRIES_CONFIG, 3);

        // Batch messages for efficiency (wait up to 10ms)
        config.put(ProducerConfig.LINGER_MS_CONFIG, 10);

        return new DefaultKafkaProducerFactory<>(config);
    }

    /**
     * Kafka Template - High-level API for sending messages
     *
     * Purpose: Provides simple send() method for publishing events
     * - Auto-serializes objects to JSON
     * - Handles async publishing
     * - Returns Future for result handling
     *
     * @return KafkaTemplate for message publishing
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}