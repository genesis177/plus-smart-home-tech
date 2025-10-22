package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.enums.SensorEventType;

public interface SensorEventHandler {

    SensorEventType getMessageType();

    void handle(SensorEvent event);
}