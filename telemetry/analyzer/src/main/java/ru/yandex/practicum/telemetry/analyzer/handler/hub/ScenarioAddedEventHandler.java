package ru.yandex.practicum.telemetry.analyzer.handler.hub;

import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.telemetry.analyzer.entity.Scenario;
import ru.yandex.practicum.telemetry.analyzer.mapper.ScenarioMapper;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {

    private final ScenarioRepository scenarioRepository;

    @Override
    public Class<ScenarioAddedEventAvro> getHandledEventType() {
        return ScenarioAddedEventAvro.class;
    }

    @Override
    protected ScenarioAddedEventAvro castPayload(final Object payload) {
        Objects.requireNonNull(payload, "Payload cannot be null.");
        log.debug("Casting payload{} to the class type {}.", payload, ScenarioAddedEventAvro.class);
        return (ScenarioAddedEventAvro) payload;
    }

    @Transactional
    @Override
    protected void handle(final String hubId, final ScenarioAddedEventAvro payload) {
        log.info("Persisting scenario {} for the hub ID {}.", payload.getName(), hubId);
        addScenario(hubId, payload);
    }

    private void addScenario(final String hubId, final ScenarioAddedEventAvro event) {
        log.debug("Adding to DB scenario {} for the hub with ID {}.", event.getName(), hubId);
        final Scenario scenarioToSave = ScenarioMapper.toScenario(event, hubId);
        final Optional<Scenario> existedScenario =
                scenarioRepository.findByHubIdAndName(hubId, event.getName());
        scenarioToSave.setId(existedScenario.map(Scenario::getId).orElse(null));

        scenarioRepository.save(scenarioToSave);
    }

}