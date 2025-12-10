package ru.yandex.practicum.telemetry.collector.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;

@Component
public class SensorEventMapper {
    public SensorEventAvro toAvro(SensorEventProto event) {
        Object payload = switch (event.getPayloadCase()) {
            case CLIMATE_SENSOR_EVENT -> toClimateSensorAvro(event.getClimateSensorEvent());
            case LIGHT_SENSOR_EVENT -> toLightSensorAvro(event.getLightSensorEvent());
            case MOTION_SENSOR_EVENT -> toMotionSensorAvro(event.getMotionSensorEvent());
            case SWITCH_SENSOR_EVENT -> toSwitchSensorAvro(event.getSwitchSensorEvent());
            case TEMPERATURE_SENSOR_EVENT -> toTemperatureSensorAvro(event.getTemperatureSensorEvent());
            default -> throw new IllegalArgumentException("Неизвестный тип события: " + event.getClass().getName());
        };
        Instant timestamp = Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos());

        return SensorEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setId(event.getId())
                .setTimestamp(timestamp)
                .setPayload(payload)
                .build();
    }

    private ClimateSensorAvro toClimateSensorAvro(ClimateSensorProto event) {
        return ClimateSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setHumidity(event.getHumidity())
                .setCo2Level(event.getCo2Level())
                .build();
    }

    private LightSensorAvro toLightSensorAvro(LightSensorProto event) {
        return LightSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setLuminosity(event.getLuminosity())
                .build();
    }

    private MotionSensorAvro toMotionSensorAvro(MotionSensorProto event) {
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setMotion(event.getMotion())
                .setVoltage(event.getVoltage())
                .build();
    }

    private SwitchSensorAvro toSwitchSensorAvro(SwitchSensorProto event) {
        return SwitchSensorAvro.newBuilder()
                .setState(event.getState())
                .build();
    }

    private TemperatureSensorAvro toTemperatureSensorAvro(TemperatureSensorProto event) {
        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setTemperatureF(event.getTemperatureF())
                .build();
    }
}
