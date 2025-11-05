package ru.yandex.practicum.telemetry.analyzer.handler.hub;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.telemetry.analyzer.entity.Device;
import ru.yandex.practicum.telemetry.analyzer.repository.DeviceRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceAddedEventHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {

    private final DeviceRepository repository;

    @Override
    public Class<DeviceAddedEventAvro> getHandledEventType() {
        return DeviceAddedEventAvro.class;
    }

    @Override
    protected DeviceAddedEventAvro castPayload(final Object payload) {
        Objects.requireNonNull(payload, "Payload cannot be null.");
        log.debug("Casting payload{} to the class type {}.", payload, DeviceAddedEventAvro.class);
        return (DeviceAddedEventAvro) payload;
    }

    @Transactional
    @Override
    protected void handle(final String hubId, final DeviceAddedEventAvro payload) {
        log.info("Persisting device ID {} to the hub ID {}.", payload.getId(), hubId);
        final Device device = mapToDevice(payload, hubId);
        repository.save(device);
    }

    private Device mapToDevice(final DeviceAddedEventAvro payload, final String hubId) {
        log.debug("Mapping payload {} to Device with hubId{}.", payload, hubId);
        return Device.builder()
                .id(payload.getId())
                .hubId(hubId)
                .build();
    }

}