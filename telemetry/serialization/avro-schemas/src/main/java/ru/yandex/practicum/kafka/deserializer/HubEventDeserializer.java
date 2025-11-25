package ru.yandex.practicum.kafka.deserializer;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

/**
 * Десериализатор для преобразования бинарных данных в объекты {@link HubEventAvro} с использованием схемы Avro.
 * @see BaseAvroDeserializer
 * @see HubEventAvro
 */


public class HubEventDeserializer extends BaseAvroDeserializer<HubEventAvro> {

    public HubEventDeserializer() {
        super(HubEventAvro.getClassSchema());
    }
}