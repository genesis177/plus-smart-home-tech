package ru.yandex.practicum.kafka.deserializer;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

/**
 * Десериализатор для преобразования бинарных данных в объекты {@link SensorsSnapshotAvro} с использованием схемы Avro.
 * @see BaseAvroDeserializer
 * @see SensorsSnapshotAvro
 */


public class SensorsSnapshotDeserializer extends BaseAvroDeserializer<SensorsSnapshotAvro> {

    public SensorsSnapshotDeserializer() {
        super(SensorsSnapshotAvro.getClassSchema());
    }
}