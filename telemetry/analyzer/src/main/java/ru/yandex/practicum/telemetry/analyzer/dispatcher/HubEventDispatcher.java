package ru.yandex.practicum.telemetry.analyzer.dispatcher;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

/**
 * Определяет соответствующий обработчик для события хаба.
 */
public interface HubEventDispatcher {

    void dispatch(HubEventAvro value);
}