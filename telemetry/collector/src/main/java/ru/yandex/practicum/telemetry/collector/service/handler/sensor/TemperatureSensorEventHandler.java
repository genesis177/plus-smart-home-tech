package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.telemetry.collector.configuration.CollectorKafkaConfig;
import ru.yandex.practicum.telemetry.collector.model.enums.SensorEventType;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.TemperatureSensorEvent;
import ru.yandex.practicum.telemetry.collector.producer.KafkaEventProducer;

/**
 * Обработчик события от датчика температуры.
 */

@Service
@Slf4j
public class TemperatureSensorEventHandler extends BaseSensorEventHandler<TemperatureSensorAvro> {

    public TemperatureSensorEventHandler(
            KafkaEventProducer producer, CollectorKafkaConfig config) {
        super(producer, config);
        log.info("TemperatureSensorEventHandler instantiated.");
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }

    @Override
    protected TemperatureSensorAvro mapToAvro(final SensorEvent event) {
        log.debug("Mapping TemperatureSensorEvent to Avro: {}", event);
        final TemperatureSensorEvent _event = (TemperatureSensorEvent) event;
        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(_event.getTemperatureC())
                .setTemperatureF(_event.getTemperatureC())
                .build();
    }

}