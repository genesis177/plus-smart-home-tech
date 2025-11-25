package ru.yandex.practicum.telemetry.aggregator.service;

import java.util.Optional;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

/**
 * Сервисный интерфейс для управления обновлениями снимков сенсоров.
 */
public interface SnapshotService {

    Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event);

}