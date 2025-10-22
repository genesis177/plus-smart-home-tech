package ru.yandex.practicum.telemetry.collector.model.enums.hub;

/**
 * Перечисление операций, которые могут использоваться в условиях для триггеров сценариев.
 * Эти операции определяют логику сравнения значений датчиков.
 */

public enum ScenarioConditionOperation {

    EQUALS,
    GREATER_THAN,
    LOWER_THAN

}