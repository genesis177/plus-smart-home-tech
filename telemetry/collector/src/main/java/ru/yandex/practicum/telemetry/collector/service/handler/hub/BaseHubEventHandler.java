package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.configuration.CollectorKafkaConfig;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaTopic;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.producer.KafkaEventProducer;

@Slf4j
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {

    protected final KafkaEventProducer producer;
    protected final String topic;

    @Autowired
    protected BaseHubEventHandler(KafkaEventProducer producer, CollectorKafkaConfig config) {
        this.producer = producer;
        this.topic = KafkaTopic.HUBS.getTopicName(config);
        log.debug("Initialized BaseHubEventHandler with topic '{}'.", topic);
    }

    protected abstract T mapToAvro(HubEvent event);

    @Override
    public void handle(HubEvent event) {
        Objects.requireNonNull(event);
        log.debug("Starting handling hub event {}", event);
        validateEventType(event);

        T payload = mapToAvro(event);
        log.info("Mapped hub event to payload: {}", payload.getClass().getName());

        HubEventAvro eventAvro = buildEventAvro(event, payload);

        producer.send(eventAvro, event.getHubId(), event.getTimestamp(), topic);
        log.info("Hub event successfully sent to topic '{}'.", topic);
    }

    private HubEventAvro buildEventAvro(HubEvent event, T payload) {
        log.debug("Building Avro event for: {}", payload.getClass().getName());
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
    }

    private void validateEventType(HubEvent event) {
        if (!event.getType().equals(getMessageType())) {
            log.warn("Wrong event type: {}.", event.getType());
            throw new IllegalArgumentException("Unknown event type: " + event.getType());
        }
    }
}