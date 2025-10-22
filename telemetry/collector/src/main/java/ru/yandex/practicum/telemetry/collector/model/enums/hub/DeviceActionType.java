package ru.yandex.practicum.telemetry.collector.model.enums.hub;

/**
 * Перечисление, представляющее возможные типы действий, которые должны быть выполнены при выполнении условий активации сценария.
 */

public enum DeviceActionType {

    ACTIVATE,
    DEACTIVATE,
    INVERSE,
    SET_VALUE

}