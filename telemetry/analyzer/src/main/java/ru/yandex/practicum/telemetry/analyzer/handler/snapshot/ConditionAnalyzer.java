package ru.yandex.practicum.telemetry.analyzer.handler.snapshot;

import java.util.Map;
import java.util.function.BiPredicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.telemetry.analyzer.entity.ScenarioCondition;
import ru.yandex.practicum.telemetry.analyzer.mapper.ScenarioMapper;

/**
 * Анализирует условия для сценариев на основе состояний датчиков.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ConditionAnalyzer {

    private static final Map<ConditionOperationAvro, BiPredicate<Integer, Integer>> COMPARISON_OPERATORS = Map.of(
            ConditionOperationAvro.EQUALS, Integer::equals,
            ConditionOperationAvro.GREATER_THAN, (expected, actual) -> actual > expected,
            ConditionOperationAvro.LOWER_THAN, (expected, actual) -> actual < expected
    );

    public boolean isConditionMet(final ScenarioCondition expectedCondition,
                                  final Map<String, SensorStateAvro> sensorsStates) {

        SensorStateAvro sensorState = sensorsStates.get(expectedCondition.getSensor().getId());
        boolean result = sensorState != null && isConditionMet(expectedCondition, sensorState);
        log.debug("Condition check for sensor {}: {}", expectedCondition.getSensor().getId(), result);
        return result;
    }

    public boolean isConditionMet(final ScenarioCondition expectedCondition,
                                  final SensorStateAvro sensorState) {

        final Integer conditionValue = expectedCondition.getValue();
        final ConditionOperationAvro operation = expectedCondition.getOperation();
        final Integer sensorValue = extractSensorValue(expectedCondition, sensorState);

        return COMPARISON_OPERATORS.getOrDefault(operation, (a, b) -> false)
                .test(conditionValue, sensorValue);
    }

    private Integer extractSensorValue(final ScenarioCondition condition,
                                       final SensorStateAvro sensorState) {
        return switch (condition.getType()) {
            case MOTION ->
                    ScenarioMapper.toInteger(((MotionSensorAvro) sensorState.getData()).getMotion());
            case LUMINOSITY -> ((LightSensorAvro) sensorState.getData()).getLuminosity();
            case SWITCH ->
                    ScenarioMapper.toInteger(((SwitchSensorAvro) sensorState.getData()).getState());
            case TEMPERATURE -> ((TemperatureSensorAvro) sensorState.getData()).getTemperatureC();
            case CO2LEVEL -> ((ClimateSensorAvro) sensorState.getData()).getCo2Level();
            case HUMIDITY -> ((ClimateSensorAvro) sensorState.getData()).getHumidity();
        };
    }
}