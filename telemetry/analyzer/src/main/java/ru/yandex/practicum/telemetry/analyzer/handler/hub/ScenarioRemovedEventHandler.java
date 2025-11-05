package ru.yandex.practicum.telemetry.analyzer.handler.hub;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScenarioRemovedEventHandler extends BaseHubEventHandler<ScenarioRemovedEventAvro> {

    private final ScenarioRepository scenarioRepository;

    @Override
    public Class<ScenarioRemovedEventAvro> getHandledEventType() {
        return ScenarioRemovedEventAvro.class;
    }

    @Override
    protected ScenarioRemovedEventAvro castPayload(final Object payload) {
        Objects.requireNonNull(payload, "Payload cannot be null.");
        log.debug("Casting payload{} to the class type {}.", payload, ScenarioRemovedEventAvro.class);
        return (ScenarioRemovedEventAvro) payload;
    }

    @Transactional
    @Override
    protected void handle(final String hubId, final ScenarioRemovedEventAvro payload) {
        log.info("Removing scenario '{}' from hub {}.", payload.getName(), hubId);
        scenarioRepository.deleteByNameAndHubId(payload.getName(), hubId);
    }

}