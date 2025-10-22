package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.telemetry.collector.configuration.CollectorKafkaConfig;
import ru.yandex.practicum.telemetry.collector.model.enums.SensorEventType;
import ru.yandex.practicum.telemetry.collector.model.sensor.ClimateSensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.producer.KafkaEventProducer;

/**
 * Обработчик события от датчика климата.
 */

@Service
@Slf4j
public class ClimateSensorEventHandler extends BaseSensorEventHandler<ClimateSensorAvro> {

    public ClimateSensorEventHandler(KafkaEventProducer producer, CollectorKafkaConfig config) {
        super(producer, config);
        log.info("ClimateSensorEventHandler instantiated.");
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }

    @Override
    protected ClimateSensorAvro mapToAvro(final SensorEvent event) {
        log.debug("Mapping ClimateSensorEvent to Avro: {}", event);
        final ClimateSensorEvent _event = (ClimateSensorEvent) event;
        return ClimateSensorAvro.newBuilder()
                .setCo2Level(_event.getCo2Level())
                .setHumidity(_event.getHumidity())
                .setTemperatureC(_event.getTemperatureC())
                .build();
    }

}