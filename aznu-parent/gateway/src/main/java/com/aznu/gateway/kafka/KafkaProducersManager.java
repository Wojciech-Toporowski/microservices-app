package com.aznu.gateway.kafka;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.UUID;


@Getter
@Component
@Slf4j
public class KafkaProducersManager {

    private final Producer<UUID , String> dateServiceProducer;
    private final Producer<UUID, String> partServiceProducer;

    @Autowired
    public KafkaProducersManager(
            @Value("${spring.kafka.producer.bootstrap-servers}") final String bootstrapServers
            ){
        Properties config = new Properties();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.RETRIES_CONFIG, 0);
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        dateServiceProducer = new KafkaProducer<>(config);
        partServiceProducer = new KafkaProducer<>(config);
    }
}
