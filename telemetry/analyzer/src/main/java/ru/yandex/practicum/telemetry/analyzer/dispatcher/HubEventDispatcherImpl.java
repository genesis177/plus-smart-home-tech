package ru.yandex.practicum.telemetry.analyzer.dispatcher;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.analyzer.handler.HubEventHandler;

/**
 * Реализация {@link HubEventDispatcher}, отвечающая за передачу событий хаба соответствующим обработчикам.
 */
@Component
@Slf4j
public class HubEventDispatcherImpl implements HubEventDispatcher {

    private final Map<Class<? extends SpecificRecordBase>, HubEventHandler> handlers;

    public HubEventDispatcherImpl(final Set<HubEventHandler> handlers) {
        this.handlers = handlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getHandledEventType, Function.identity()));
    }

    @Override
    public void dispatch(final HubEventAvro event) {
        final Class<?> handlerType = event.getPayload().getClass();
        if (handlers.containsKey(handlerType)) {
            try {
                handlers.get(handlerType).handle(event);
            } catch (Exception e) {
                log.warn("No handler found for hub event: {}.", handlerType);
            }
        } else {
            throw new IllegalArgumentException("No handler found for hub event: {}." + handlerType);
        }
    }
}