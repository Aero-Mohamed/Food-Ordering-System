package com.food.kafka.producer.service.impl;

import com.food.kafka.producer.exception.KafkaProducerException;
import com.food.kafka.producer.service.KafkaProducer;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class KafkaProducerImpl<K extends Serializable, V extends SpecificRecordBase> implements KafkaProducer<K, V> {

    private final KafkaTemplate<K, V> kafkaTemplate;

    public KafkaProducerImpl(KafkaTemplate<K, V> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public CompletableFuture<SendResult<K, V>> send(String topicName, K key, V message) {
        log.info("Sending message to topic: {} with key: {} and message: {}", topicName, key, message);
        try {
            return kafkaTemplate.send(topicName, key, message);
        } catch (Exception e) {
            log.error("Error sending message to topic: {} with key: {} and message: {}", topicName, key, message, e);
            throw new KafkaProducerException("Error sending message to topic: " + topicName + " with key: " + key + " and message: " + message);
        }
    }

    @PreDestroy
    public void close() {
        if(kafkaTemplate != null) {
            log.info("Closing Kafka producer");
            kafkaTemplate.destroy();
        }
    }
}
