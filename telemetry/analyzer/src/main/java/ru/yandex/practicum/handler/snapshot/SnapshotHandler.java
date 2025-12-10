package ru.yandex.practicum.handler.snapshot;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.service.HubRouterGrpcClient;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SnapshotHandler {
    ActionRepository actionRepository;
    ConditionRepository conditionRepository;
    ScenarioRepository scenarioRepository;
    HubRouterGrpcClient hubRouterClient;

    public void handleSnapshot(SensorsSnapshotAvro snapshot) {
        log.debug("Обработка снапшота: {}", snapshot);
        var sensorState = snapshot.getSensorsState();

        scenarioRepository.findByHubId(snapshot.getHubId()).stream()
                .filter(scenario -> conditionsMatch(scenario, sensorState))
                .forEach(scenario -> {
                    log.info("Отправка действий из сценария {}", scenario);
                    actionRepository.findAllByScenario(scenario)
                            .forEach(hubRouterClient::sendRequest);
                });
    }

    private boolean conditionsMatch(Scenario scenario, Map<String, SensorStateAvro> sensorState) {
        return conditionRepository.findAllByScenario(scenario).stream()
                .allMatch(condition -> evaluateCondition(condition, sensorState.get(condition.getSensor().getId())));
    }

    private boolean evaluateCondition(Condition condition, SensorStateAvro state) {
        if (state == null || state.getData() == null) return false;

        return switch (condition.getType()) {
            case MOTION -> check(condition, ((MotionSensorAvro) state.getData()).getMotion() ? 1 : 0);
            case LUMINOSITY -> check(condition, ((LightSensorAvro) state.getData()).getLuminosity());
            case SWITCH -> check(condition, ((SwitchSensorAvro) state.getData()).getState() ? 1 : 0);
            case TEMPERATURE -> check(condition, ((ClimateSensorAvro) state.getData()).getTemperatureC());
            case CO2LEVEL -> check(condition, ((ClimateSensorAvro) state.getData()).getCo2Level());
            case HUMIDITY -> check(condition, ((ClimateSensorAvro) state.getData()).getHumidity());
            default -> false;
        };
    }

    private boolean check(Condition condition, Integer value) {
        var expected = condition.getValue();
        return switch (condition.getOperation()) {
            case EQUALS -> Objects.equals(value, expected);
            case GREATER_THAN -> value > expected;
            case LOWER_THAN -> value < expected;
            default -> false;
        };
    }
}
