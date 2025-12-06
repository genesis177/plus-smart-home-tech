package ru.yandex.practicum.handler.hub.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.handler.hub.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScenarioAddedHandler implements HubEventHandler {
    ActionRepository actionRepository;
    ConditionRepository conditionRepository;
    ScenarioRepository scenarioRepository;
    SensorRepository sensorRepository;

    @Override
    public String getType() {
        return ScenarioAddedEventAvro.class.getSimpleName();
    }

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        var payload = (ScenarioAddedEventAvro) event.getPayload();
        var scenario = scenarioRepository
                .findByHubIdAndName(event.getHubId(), payload.getName())
                .orElseGet(() -> scenarioRepository.save(Scenario.builder()
                        .name(payload.getName())
                        .hubId(event.getHubId())
                        .build()));

        if (hasSensors(payload.getActions().stream().map(DeviceActionAvro::getSensorId).toList(), event.getHubId())) {
            actionRepository.saveAll(mapActions(payload, scenario));
        }

        if (hasSensors(payload.getConditions().stream().map(ScenarioConditionAvro::getSensorId).toList(), event.getHubId())) {
            conditionRepository.saveAll(mapConditions(payload, scenario));
        }

        log.info("Scenario {} added", scenario.getName());
    }

    private boolean hasSensors(List<String> sensorIds, String hubId) {
        return sensorRepository.existsByIdInAndHubId(sensorIds, hubId);
    }

    private Set<Action> mapActions(ScenarioAddedEventAvro event, Scenario scenario) {
        return event.getActions().stream()
                .map(a -> Action.builder()
                        .sensor(sensorRepository.findById(a.getSensorId()).orElseThrow())
                        .scenario(scenario)
                        .type(a.getType())
                        .value(a.getValue())
                        .build())
                .collect(Collectors.toSet());
    }

    private Set<Condition> mapConditions(ScenarioAddedEventAvro event, Scenario scenario) {
        return event.getConditions().stream()
                .map(c -> Condition.builder()
                        .sensor(sensorRepository.findById(c.getSensorId()).orElseThrow())
                        .scenario(scenario)
                        .type(c.getType())
                        .operation(c.getOperation())
                        .value(convertValue(c.getValue()))
                        .build())
                .collect(Collectors.toSet());
    }

    private Integer convertValue(Object value) {
        if (value == null) return null;
        return (value instanceof Integer) ? (Integer) value : ((Boolean) value ? 1 : 0);
    }
}
