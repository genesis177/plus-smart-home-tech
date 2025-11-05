package ru.yandex.practicum.telemetry.analyzer.mapper;

import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.telemetry.analyzer.entity.Device;
import ru.yandex.practicum.telemetry.analyzer.entity.DeviceAction;
import ru.yandex.practicum.telemetry.analyzer.entity.Scenario;
import ru.yandex.practicum.telemetry.analyzer.entity.ScenarioCondition;

/**
 * Класс-утиль для сопоставления структур данных, связанных со сценариями.
 */

@UtilityClass
@Slf4j
public class ScenarioMapper {

    public Scenario toScenario(final ScenarioAddedEventAvro payload, final String hubId) {
        log.debug("Mapping Scenario {} for hubId {}.", payload.getName(), hubId);

        final Scenario scenario = Scenario.builder()
                .name(payload.getName())
                .hubId(hubId)
                .build();
        final List<ScenarioCondition> conditions = toScenarioCondition(payload.getConditions(), scenario);
        final List<DeviceAction> actions = ActionMapper.toDeviceAction(payload.getActions(), scenario);
        scenario.setConditions(conditions);
        scenario.setActions(actions);
        return scenario;
    }

    public List<ScenarioCondition> toScenarioCondition(
            final List<ScenarioConditionAvro> conditions, final Scenario scenario) {
        return conditions.stream()
                .map(conditionAvro -> toScenarioCondition(conditionAvro, scenario))
                .toList();
    }

    public ScenarioCondition toScenarioCondition(final ScenarioConditionAvro conditionAvro,
                                                 final Scenario scenario) {
        return ScenarioCondition.builder()
                .sensor(Device.builder()
                        .hubId(scenario.getHubId())
                        .id(conditionAvro.getSensorId())
                        .build())
                .type(conditionAvro.getType())
                .operation(conditionAvro.getOperation())
                .value(toInteger(conditionAvro.getValue()))
                .scenario(scenario)
                .build();
    }

    public Integer toInteger(final Object value) {
        return switch (value) {
            case Integer i -> i;
            case Boolean b -> b ? 1 : 0;
            default -> null;
        };
    }

}