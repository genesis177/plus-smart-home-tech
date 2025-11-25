package ru.yandex.practicum.telemetry.analyzer.mapper;

import com.google.protobuf.Timestamp;
import java.time.Instant;
import java.util.List;
import lombok.experimental.UtilityClass;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.telemetry.analyzer.entity.Device;
import ru.yandex.practicum.telemetry.analyzer.entity.DeviceAction;
import ru.yandex.practicum.telemetry.analyzer.entity.Scenario;

/**
 * Вспомогательный класс для сопоставления действий устройств с соответствующими структурами данных.
 */
@UtilityClass
public class ActionMapper {

    public List<DeviceAction> toDeviceAction(final List<DeviceActionAvro> actions,
                                             final Scenario scenario) {
        return actions.stream()
                .map(actionAvro -> toDeviceAction(actionAvro, scenario))
                .toList();
    }

    public DeviceAction toDeviceAction(final DeviceActionAvro action, final Scenario scenario) {
        return DeviceAction.builder()
                .type(action.getType())
                .value(action.getValue())
                .scenario(scenario)
                .sensor(Device.builder()
                        .hubId(scenario.getHubId())
                        .id(action.getSensorId())
                        .build())
                .build();
    }

    public DeviceActionRequest toDeviceActionRequest(
            final DeviceAction action, final String hubId, final String scenarioName) {
        final Instant timestamp = Instant.now();
        return DeviceActionRequest.newBuilder()
                .setHubId(hubId)
                .setScenarioName(scenarioName)
                .setAction(DeviceActionProto.newBuilder()
                        .setSensorId(action.getSensor().getId())
                        .setType(ActionTypeProto.valueOf(action.getType().name()))
                        .setValue(action.getValue())
                        .build())
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(timestamp.getEpochSecond())
                        .setNanos(timestamp.getNano())
                        .build())
                .build();
    }

}