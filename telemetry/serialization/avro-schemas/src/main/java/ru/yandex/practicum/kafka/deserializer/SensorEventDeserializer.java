package ru.yandex.practicum.kafka.deserializer;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

/**
 * Десериализатор для преобразования бинарных данных в объекты {@link SensorEventAvro} с использованием схемы Avro.
 * @see BaseAvroDeserializer
 * @see SensorEventAvro
 */


public class SensorEventDeserializer  extends BaseAvroDeserializer<SensorEventAvro> {

    public SensorEventDeserializer() {
        super(SensorEventAvro.getClassSchema());
    }
}