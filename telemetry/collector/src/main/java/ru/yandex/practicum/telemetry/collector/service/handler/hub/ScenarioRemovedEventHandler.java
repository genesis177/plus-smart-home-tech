package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.telemetry.collector.configuration.CollectorKafkaConfig;
import ru.yandex.practicum.telemetry.collector.model.enums.hub.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.ScenarioRemovedEvent;
import ru.yandex.practicum.telemetry.collector.producer.KafkaEventProducer;

/**
 * Обработчик события удаления автоматизированного сценария от хаба.
 */

@Service
@Slf4j
public class ScenarioRemovedEventHandler extends BaseHubEventHandler<ScenarioRemovedEventAvro> {

    public ScenarioRemovedEventHandler(KafkaEventProducer producer, CollectorKafkaConfig config) {
        super(producer, config);
        log.info("ScenarioRemovedEventHandler instantiated.");
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_REMOVED;
    }

    @Override
    protected ScenarioRemovedEventAvro mapToAvro(final HubEvent event) {
        log.debug("Mapping ScenarioRemovedEvent to Avro: {}", event);
        final ScenarioRemovedEvent _event = (ScenarioRemovedEvent) event;
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(_event.getName())
                .build();
    }
}