package ru.yandex.practicum.telemetry.aggregator.service;

import java.util.HashMap;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.aggregator.repository.SnapshotRepository;

/**
 * Реализация сервиса для управления обновлениями снимков (состояний) сенсоров.
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class SnapshotServiceImpl implements SnapshotService {

    private final SnapshotRepository snapshots;

    /**
     * Обновляет состояние снимка (snapshot) сенсора на основе переданного события сенсора.
     * <p>
     * Этот метод выполняет следующие операции:
     * <ol>
     *   <li>
     *     Пытается найти существующий снимок для хаба, связанного с событием.
     *     Если снимок не найден — создаётся новый.
     *   </li>
     *   <li>
     *     Проверяет, содержит ли снимок уже данные состояния сенсора с указанным ID события.
     *     Если текущие данные более новые или идентичные — обновление пропускается,
     *     и возвращается {@code Optional.empty()}.
     *   </li>
     *   <li>
     *     Если данные необходимо обновить — создаётся новое состояние сенсора,
     *     которое добавляется в снимок. Метка времени снимка обновляется,
     *     чтобы соответствовать метке времени события.
     *   </li>
     *   <li>
     *     Обновлённый снимок сохраняется в репозитории
     *     и возвращается в виде объекта {@code Optional}.
     *   </li>
     * </ol>
     *
     * @param event входящее событие сенсора
     * @return объект {@code Optional}, содержащий обновлённый снимок,
     * или пустой, если обновление не потребовалось.
     */
    @Override
    public Optional<SensorsSnapshotAvro> updateState(final SensorEventAvro event) {
        log.info("Updating snapshot for hubId={} with event - sensorId={}", event.getHubId(), event.getId());

        final SensorsSnapshotAvro snapshot = snapshots.findByHubId(event.getHubId())
                .orElseGet(() -> buildSnapshot(event));

        if (isCurrentSnapshotValid(snapshot, event)) {
            log.info("No update required for hubId={} and sensorId={}", event.getHubId(), event.getId());
            return Optional.empty();
        }

        updateSnapshotData(snapshot, event);

        snapshots.save(snapshot);
        return Optional.of(snapshot);
    }

    private void updateSnapshotData(final SensorsSnapshotAvro snapshot, final SensorEventAvro event) {
        log.debug("Updating snapshot with new sensor state {}", event.getPayload());
        final SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        snapshot.getSensorsState().put(event.getId(), newState);
        snapshot.setTimestamp(event.getTimestamp());
    }

    private boolean isCurrentSnapshotValid(final SensorsSnapshotAvro currentSnapshot,
                                           final SensorEventAvro event) {
        log.debug("Validating current snapshot data {} against new event data {}", currentSnapshot, event);
        final SensorStateAvro currentState = currentSnapshot.getSensorsState().get(event.getId());

        return currentState != null &&
                (!currentState.getTimestamp().isBefore(event.getTimestamp()) ||
                        currentState.getData().equals(event.getPayload()));
    }

    private SensorsSnapshotAvro buildSnapshot(final SensorEventAvro event) {
        log.debug("Creating new snapshot with reading from the sensor event {}.", event);
        return SensorsSnapshotAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setSensorsState(new HashMap<>())
                .build();
    }
}