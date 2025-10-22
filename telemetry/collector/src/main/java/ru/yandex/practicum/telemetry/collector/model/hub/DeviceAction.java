package ru.yandex.practicum.telemetry.collector.model.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.collector.model.enums.hub.DeviceActionType;

/**
 * Представляет действие, которое устройство должно выполнить в рамках сценария. Это действие включает ID
 * устройства, выполняемое действие и необязательное значение для этого действия.
 */

@Setter
@Getter
@ToString
public class DeviceAction {

    @NotBlank
    private String sensorId;

    @NotNull
    private DeviceActionType type;

    private Integer value;

}