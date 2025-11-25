package ru.yandex.practicum.telemetry.collector.configuration;

import lombok.Getter;

/**
 * Перечисление, представляющее темы Kafka, используемые в системе. Каждое значение перечисления
 * соответствует конкретной теме и предоставляет способ получить название темы из конфигурации
 * приложения на основе ключа перечисления.
 */

@Getter
public enum KafkaTopic {
    SENSORS("sensors"),
    HUBS("hubs");

    private final String topicKey;

    KafkaTopic(String topicKey) {
        this.topicKey = topicKey;
    }

    public String getTopicName(CollectorKafkaConfig config) {
        return config.getTopic(this.topicKey);
    }
}