package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.telemetry.collector.configuration.CollectorKafkaConfig;
import ru.yandex.practicum.telemetry.collector.model.enums.SensorEventType;
import ru.yandex.practicum.telemetry.collector.model.sensor.LightSensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.producer.KafkaEventProducer;

/**
 * Обработчик события от датчика освещённости.
 */

@Service
@Slf4j
public class LightSensorEventHandler extends BaseSensorEventHandler<LightSensorAvro> {

    public LightSensorEventHandler(KafkaEventProducer producer, CollectorKafkaConfig config) {
        super(producer, config);
        log.info("LightSensorEventHandler instantiated.");
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }

    @Override
    protected LightSensorAvro mapToAvro(final SensorEvent event) {
        log.debug("Mapping LightSensorEvent to Avro: {}", event);
        final LightSensorEvent _event = (LightSensorEvent) event;
        return LightSensorAvro.newBuilder()
                .setLinkQuality(_event.getLinkQuality())
                .setLuminosity(_event.getLuminosity())
                .build();
    }

}