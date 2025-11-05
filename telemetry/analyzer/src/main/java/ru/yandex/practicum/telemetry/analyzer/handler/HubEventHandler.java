package ru.yandex.practicum.telemetry.analyzer.handler;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubEventHandler {

    Class<? extends SpecificRecordBase> getHandledEventType();

    void handle(HubEventAvro event);

}