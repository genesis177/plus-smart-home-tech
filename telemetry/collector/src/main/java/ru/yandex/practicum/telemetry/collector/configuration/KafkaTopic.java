package ru.yandex.practicum.telemetry.collector.configuration;

import lombok.Getter;

/**
 * Перечисление (enum), представляющее топики Kafka, используемые в системе. Каждое значение enum соответствует конкретному топику и
 * предоставляет способ получить название топика из конфигурации приложения на основе ключа enum.
 */

@Getter
public enum KafkaTopic {
    SENSORS("sensors"),
    HUBS("hubs");

    private final String topicKey;

    KafkaTopic(String topicKey) {
        this.topicKey = topicKey;
    }

    /**
     * Получить имя топика по умолчанию, связанное с этим значением enum.
     *
     * @param config Экземпляр CollectorKafkaConfig для доступа к настройкам топиков.
     * @return Соответствующее имя топика из конфигурации.
     */

    public String getTopicName(CollectorKafkaConfig config) {
        return config.getTopic(this.topicKey);
    }
}