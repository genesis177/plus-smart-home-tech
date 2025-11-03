package ru.yandex.practicum.telemetry.aggregator.repository;

import java.util.Optional;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

/**
 * Интерфейс репозитория для управления данными снимков (состояний) сенсоров.
 * Предоставляет методы для сохранения и получения снимков на основе идентификатора хаба.
 */
public interface SnapshotRepository {

    void save(SensorsSnapshotAvro snapshot);

    Optional<SensorsSnapshotAvro> findByHubId(String hubId);

}