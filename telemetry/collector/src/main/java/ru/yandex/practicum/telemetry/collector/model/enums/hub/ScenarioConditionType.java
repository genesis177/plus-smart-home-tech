package ru.yandex.practicum.telemetry.collector.model.enums.hub;

/**
 * Перечисление, представляющее типы условий, которые могут использоваться в сценариях.
 * Каждое условие связано с конкретным типом датчика.
 */

public enum ScenarioConditionType {

    MOTION,
    LUMINOSITY,
    SWITCH,
    TEMPERATURE,
    CO2LEVEL,
    HUMIDITY
}