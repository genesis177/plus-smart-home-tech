package ru.yandex.practicum.telemetry.collector.model.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.collector.model.enums.hub.DeviceType;
import ru.yandex.practicum.telemetry.collector.model.enums.hub.HubEventType;

/**
 * Представляет событие, которое возникает при добавлении нового устройства в систему. Это событие содержит
 * информацию о типе устройства и его идентификаторе.
 */

@Setter
@Getter
@ToString(callSuper = true)
public class DeviceAddedEvent extends HubEvent {

    @NotBlank
    private String id;

    @NotNull
    private DeviceType deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}