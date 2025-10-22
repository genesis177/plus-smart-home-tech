package ru.yandex.practicum.telemetry.collector.model.hub;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.collector.model.enums.hub.HubEventType;

/**
 * Представляет событие, которое возникает при добавлении нового сценария в систему. Это событие содержит
 * информацию о названии сценария, условиях и действиях.
 */

@Setter
@Getter
@ToString(callSuper = true)
public class ScenarioAddedEvent extends HubEvent {

    @NotBlank
    @Size(min = 3)
    private String name;

    @NotNull
    @Valid
    private List<ScenarioCondition> conditions;

    @NotNull
    @Valid
    private List<DeviceAction> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}