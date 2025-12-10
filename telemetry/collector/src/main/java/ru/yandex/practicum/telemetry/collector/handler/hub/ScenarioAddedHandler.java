package ru.yandex.practicum.telemetry.collector.handler.hub;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.telemetry.collector.kafka.KafkaEventProducer;
import ru.yandex.practicum.telemetry.collector.mapper.HubEventMapper;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioAddedHandler implements HubEventHandler {
    final KafkaEventProducer kafkaEventProducer;
    final HubEventMapper hubEventMapper;

    @Value("telemetry.hubs.v1")
    String topic;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEventProto eventProto) {
        Instant timestamp = Instant.ofEpochSecond(eventProto.getTimestamp().getSeconds(), eventProto.getTimestamp().getNanos());
        kafkaEventProducer.send(topic, eventProto.getHubId(), timestamp, hubEventMapper.toAvro(eventProto));
    }
}
