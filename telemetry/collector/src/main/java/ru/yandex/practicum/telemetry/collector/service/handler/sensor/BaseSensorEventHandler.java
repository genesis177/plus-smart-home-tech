package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.configuration.CollectorKafkaConfig;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaTopic;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.producer.KafkaEventProducer;

/**
 * Базовый класс для обработки событий датчиков.
 *
 * @param <T> Тип записи Avro, к которой будет привязано событие датчика.
 */

@Slf4j
public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {

    protected final KafkaEventProducer producer;
    protected final String topic;

    @Autowired
    protected BaseSensorEventHandler(KafkaEventProducer producer, CollectorKafkaConfig config) {
        this.producer = producer;
        this.topic = KafkaTopic.SENSORS.getTopicName(config);
        log.debug("Initialized BaseSensorEventHandler with topic '{}'.", topic);
    }

    protected abstract T mapToAvro(SensorEvent event);

    @Override
    public void handle(SensorEvent event) {
        Objects.requireNonNull(event);
        log.debug("Starting handling event {}", event);
        validateEventType(event);

        T payload = mapToAvro(event);
        log.info("Mapped event to payload: {}", payload.getClass().getName());

        SensorEventAvro eventAvro = buildEventAvro(event, payload);

        producer.send(eventAvro, event.getHubId(), event.getTimestamp(), topic);
        log.info("Sensor event successfully sent to topic '{}'.", topic);
    }

    private SensorEventAvro buildEventAvro(SensorEvent event, T payload) {
        log.debug("Building Avro event for: {}", payload.getClass().getName());
        return SensorEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setId(event.getId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
    }

    private void validateEventType(SensorEvent event) {
        if (!event.getType().equals(getMessageType())) {
            log.warn("Wrong event type: {}.", event.getType());
            throw new IllegalArgumentException("Unknown event type: " + event.getType());
        }
    }
}