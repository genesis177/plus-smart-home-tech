package ru.yandex.practicum.telemetry.collector.model.enums;

/**
 * Представляет различные типы событий датчиков.
 * <p>
 * Этот enum определяет различные типы событий датчиков, которые могут генерироваться
 * в системе телеметрии. Каждый тип соответствует конкретному событию,
 * связанному с датчиком, например, обнаружение движения, измерение температуры,
 * определение уровня освещенности, данные о климате и состояние выключателя.
 */

public enum SensorEventType {

    MOTION_SENSOR_EVENT,
    TEMPERATURE_SENSOR_EVENT,
    LIGHT_SENSOR_EVENT,
    CLIMATE_SENSOR_EVENT,
    SWITCH_SENSOR_EVENT

}