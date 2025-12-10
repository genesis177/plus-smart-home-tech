package ru.yandex.practicum.mapper;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Sensor;

import java.time.Instant;

@Component
public class Mapper {
    public Sensor toSensor(HubEventAvro event) {
        DeviceAddedEventAvro deviceAddedEvent = (DeviceAddedEventAvro) event.getPayload();
        return Sensor.builder().id(deviceAddedEvent.getId()).hubId(event.getHubId()).build();
    }

    public DeviceActionRequest toActionRequest(Action action) {
        Instant now = Instant.now();
        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(now.getEpochSecond())
                .setNanos(now.getNano())
                .build();

        return DeviceActionRequest.newBuilder()
                .setHubId(action.getScenario().getHubId())
                .setScenarioName(action.getScenario().getName())
                .setAction(toDeviceActionProto(action))
                .setTimestamp(timestamp)
                .build();
    }

    private DeviceActionProto toDeviceActionProto(Action action) {
        ActionTypeProto actionTypeProto =
                switch (action.getType()) {
                    case ACTIVATE -> ActionTypeProto.ACTIVATE;
                    case DEACTIVATE -> ActionTypeProto.DEACTIVATE;
                    case INVERSE -> ActionTypeProto.INVERSE;
                    case SET_VALUE -> ActionTypeProto.SET_VALUE;
                };
        return DeviceActionProto.newBuilder()
                .setSensorId(action.getSensor().getId())
                .setType(actionTypeProto)
                .setValue(action.getValue())
                .build();
    }

}
