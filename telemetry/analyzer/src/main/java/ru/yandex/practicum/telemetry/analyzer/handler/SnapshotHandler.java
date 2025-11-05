package ru.yandex.practicum.telemetry.analyzer.handler;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

/**
 * Interface for handling sensor snapshot events.
 */
public interface SnapshotHandler {

    void handle(SensorsSnapshotAvro event);

}