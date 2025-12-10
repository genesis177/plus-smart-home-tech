package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class SensorSnapshotService {
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();


    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        String sensorId = event.getId();
        Instant timestamp = event.getTimestamp();

        SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(event.getHubId(), k ->
                new SensorsSnapshotAvro(event.getHubId(), timestamp, new HashMap<>()));
        Map<String, SensorStateAvro> sensorsState = snapshot.getSensorsState();
        SensorStateAvro oldState = sensorsState.get(event.getId());

        if (oldState != null) {
            Instant oldTimestamp = oldState.getTimestamp();

            if (oldTimestamp.isAfter(timestamp)) {
                log.warn("Пропущено событие от датчика с идентификатором: {} с oldTimestamp: {} > timestamp: {}",
                        sensorId, oldTimestamp, timestamp);
                return Optional.empty();
            }

            if (isUnchanged(oldState.getData(), event.getPayload())) {
                log.info("Состояние датчика с id :{}  не изменилось", sensorId);
                return Optional.empty();
            }

            log.debug("Состояния датчика c id {} изменилось", sensorId);
        } else {
            log.debug("Добавлено состояние датчика: {}", sensorId);
        }

        SensorStateAvro newState = new SensorStateAvro(timestamp, event.getPayload());
        sensorsState.put(sensorId, newState);
        snapshot.setTimestamp(timestamp);

        log.info("Обнлвено состояние датчика с id: {}", sensorId);
        return Optional.of(snapshot);
    }


    private Boolean isUnchanged(Object oldPayload, Object newPayload) {
        if (!oldPayload.getClass().equals(newPayload.getClass())) {
            log.warn("Неодинаковые типы:  {} != {}", oldPayload.getClass(), newPayload.getClass());
            return false;
        }

        switch (oldPayload) {
            case ClimateSensorAvro oldClimate when newPayload instanceof ClimateSensorAvro newClimate -> {
                log.debug("Проверка датчика климата");
                return oldClimate.getTemperatureC() == newClimate.getTemperatureC()
                        && oldClimate.getHumidity() == newClimate.getHumidity()
                        && oldClimate.getCo2Level() == newClimate.getCo2Level();
            }
            case LightSensorAvro oldLight when newPayload instanceof LightSensorAvro newLight -> {
                log.debug("Проверка датчика света");
                return oldLight.getLinkQuality() == newLight.getLinkQuality()
                        && oldLight.getLuminosity() == newLight.getLuminosity();
            }
            case MotionSensorAvro oldMotion when newPayload instanceof MotionSensorAvro newMotion -> {
                log.debug("Проверка датчика движения");
                return oldMotion.getLinkQuality() == newMotion.getLinkQuality()
                        && oldMotion.getMotion() == newMotion.getMotion()
                        && oldMotion.getVoltage() == newMotion.getVoltage();
            }
            case SwitchSensorAvro oldSwitch when newPayload instanceof SwitchSensorAvro newSwitch -> {
                log.debug("Проверка переключателя");
                return oldSwitch.getState() == newSwitch.getState();
            }
            case TemperatureSensorAvro oldTemp when newPayload instanceof TemperatureSensorAvro neTemp -> {
                log.debug("Провекра датчика температуры");
                return oldTemp.getTemperatureC() == neTemp.getTemperatureC()
                        && oldTemp.getTemperatureF() == neTemp.getTemperatureF();
            }
            default -> {
                log.warn("Неизвестный тип датчика: {}", oldPayload.getClass());
                return false;
            }
        }
    }
}
