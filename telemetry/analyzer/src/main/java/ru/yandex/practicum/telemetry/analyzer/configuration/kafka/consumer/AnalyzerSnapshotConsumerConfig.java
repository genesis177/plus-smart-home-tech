package ru.yandex.practicum.telemetry.analyzer.configuration.kafka.consumer;

import jakarta.annotation.PostConstruct;
import java.util.Properties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

@Getter
@Setter
@ConfigurationProperties(prefix = "analyzer.kafka.consumers.snapshot")
@Configuration
@Slf4j
public class AnalyzerSnapshotConsumerConfig {

    private String bootstrapServers;
    private String groupId;
    private String autoOffsetReset;
    private String keyDeserializer;
    private String valueDeserializer;
    private long consumeAttemptTimeoutMs;

    @Bean
    public KafkaConsumer<String, SensorsSnapshotAvro> kafkaConsumer() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);

        return new KafkaConsumer<>(config);
    }

    @PostConstruct
    private void validateConfig() {

        if (bootstrapServers == null || groupId == null ||
                autoOffsetReset == null || keyDeserializer == null ||
                valueDeserializer == null) {
            log.error("Invalid Kafka snapshot consumer configuration.");
            throw new IllegalStateException(
                    "Missing required Kafka configuration for the snapshot consumer.");
        }
        log.debug("Kafka snapshot consumer configuration validated successfully.");
    }

}