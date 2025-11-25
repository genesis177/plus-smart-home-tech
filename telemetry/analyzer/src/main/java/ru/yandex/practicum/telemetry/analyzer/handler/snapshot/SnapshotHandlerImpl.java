package ru.yandex.practicum.telemetry.analyzer.handler.snapshot;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.entity.Scenario;
import ru.yandex.practicum.telemetry.analyzer.grpc.HubRouterClient;
import ru.yandex.practicum.telemetry.analyzer.handler.SnapshotHandler;
import ru.yandex.practicum.telemetry.analyzer.mapper.ActionMapper;
import ru.yandex.practicum.telemetry.analyzer.repository.DeviceRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;

/**
 * Реализация {@link SnapshotHandler}, которая обрабатывает снимки датчиков, оценивает сценарии
 * на основе условий и отправляет запросы на выполнение действий через gRPC.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SnapshotHandlerImpl implements SnapshotHandler {

    private final ScenarioRepository scenarioRepository;
    private final DeviceRepository sensorRepository;
    private final HubRouterClient hubRouterClient;

    private final ConditionAnalyzer conditionAnalyzer;

    @Override
    public void handle(final SensorsSnapshotAvro snapshot) {
        final String hubId = snapshot.getHubId();
        log.debug("Processing snapshot from the hub {}, timestamp {}.", hubId, snapshot.getTimestamp());
        scenarioRepository.findByHubId(hubId).stream()
                .filter(scenario -> isValidScenario(scenario) && isTriggeredScenario(scenario, snapshot))
                .forEach(this::sendActionRequests);
    }

    private boolean isValidScenario(final Scenario scenario) {
        Set<String> scenarioDeviceIds = scenario.getConditions().stream()
                .map(condition -> condition.getSensor().getId()).collect(
                        Collectors.toSet());

        long count = sensorRepository.countByIdInAndHubId(scenarioDeviceIds, scenario.getHubId());
        return count == scenarioDeviceIds.size();
    }

    private boolean isTriggeredScenario(final Scenario scenario,
                                        final SensorsSnapshotAvro snapshot) {
        return scenario.getConditions().stream()
                .allMatch(
                        condition -> conditionAnalyzer.isConditionMet(condition, snapshot.getSensorsState()));
    }


    private void sendActionRequests(final Scenario scenario) {
        scenario.getActions().stream()
                .map(action ->
                        ActionMapper.toDeviceActionRequest(action, scenario.getHubId(), scenario.getName()))
                .forEach(hubRouterClient::send);
    }
}