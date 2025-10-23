package ru.yandex.practicum.telemetry.collector.model.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.collector.model.enums.hub.ScenarioConditionOperation;
import ru.yandex.practicum.telemetry.collector.model.enums.hub.ScenarioConditionType;

/**
 * Представляет условие для автоматизированного сценария, которое включает информацию о датчике,
 * условия операции, которые должны применяться, и значении, используемом в условии.
 */

@Setter
@Getter
@ToString
public class ScenarioCondition {

    @NotBlank
    private String sensorId;

    @NotNull
    private ScenarioConditionType type;

    @NotNull
    private ScenarioConditionOperation operation;

    private Integer value;

}