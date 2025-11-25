package ru.yandex.practicum.telemetry.analyzer.handler.hub;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.analyzer.handler.HubEventHandler;

/**
 * Базовый класс для обработки сообщений HubEventAvro.
 *
 * @param <T> Тип полезной нагрузки Avro-события.
 */

@Slf4j
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {

    protected abstract T castPayload(Object payload);

    protected abstract void handle(String hubId, T payload);

    @Override
    public void handle(final HubEventAvro event) {
        log.debug("Handling hub event {}.", event);

        validateEventType(event);

        final String hubId = event.getHubId();
        final T payload = castPayload(event.getPayload());

        handle(hubId, payload);
    }

    private void validateEventType(final HubEventAvro event) {
        Objects.requireNonNull(event, "Event cannot be null.");
        if (!getHandledEventType().equals(event.getPayload().getClass())) {
            log.error("Invalid event type received: {}; expected: {}.",
                    event.getPayload().getClass(), getHandledEventType());
            throw new IllegalArgumentException("Unknown event type: " + event.getPayload().getClass());
        }
    }
}