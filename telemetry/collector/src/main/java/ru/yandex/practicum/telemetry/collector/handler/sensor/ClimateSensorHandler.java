package ru.yandex.practicum.telemetry.collector.handler.sensor;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.kafka.KafkaEventProducer;
import ru.yandex.practicum.telemetry.collector.mapper.SensorEventMapper;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClimateSensorHandler implements SensorEventHandler {
    final KafkaEventProducer kafkaEventProducer;
    final SensorEventMapper sensorEventMapper;

    @Value("${kafka.sensor-topic}")
    String topic;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto eventProto) {
        Instant timestamp = Instant.ofEpochSecond(eventProto.getTimestamp().getSeconds(), eventProto.getTimestamp().getNanos());
        kafkaEventProducer.send(topic, eventProto.getHubId(), timestamp, sensorEventMapper.toAvro(eventProto));
    }
}
