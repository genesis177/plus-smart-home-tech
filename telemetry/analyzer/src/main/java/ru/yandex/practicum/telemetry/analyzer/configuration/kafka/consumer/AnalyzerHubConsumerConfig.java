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
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Getter
@Setter
@ConfigurationProperties(prefix = "analyzer.kafka.consumers.hubs")
@Configuration
@Slf4j
public class AnalyzerHubConsumerConfig {

    private String bootstrapServers;
    private String groupId;
    private String autoOffsetReset;
    private boolean enableAutoCommit;
    private String keyDeserializer;
    private String valueDeserializer;
    private long consumeAttemptTimeoutMs;

    @Bean
    public KafkaConsumer<String, HubEventAvro> kafkaHubsConsumer() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);

        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);

        return new KafkaConsumer<>(config);
    }

    @PostConstruct
    private void validateConfig() {
        if (bootstrapServers == null || bootstrapServers.isEmpty()) {
            throw new IllegalArgumentException("Kafka bootstrapServers for hubs consumer cannot be null or empty");
        }
        if (groupId == null || groupId.isEmpty()) {
            throw new IllegalArgumentException("Kafka groupId for hubs consumer cannot be null or empty");
        }
    }

}