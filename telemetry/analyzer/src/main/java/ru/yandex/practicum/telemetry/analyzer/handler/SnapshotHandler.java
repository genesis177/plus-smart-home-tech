package ru.yandex.practicum.telemetry.analyzer.handler;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

/**
 * Интерфейс для обработки событий снимков сенсоров.
 */

public interface SnapshotHandler {

    void handle(SensorsSnapshotAvro event);

}