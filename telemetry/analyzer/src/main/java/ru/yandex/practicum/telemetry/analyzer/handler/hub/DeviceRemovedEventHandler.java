package ru.yandex.practicum.telemetry.analyzer.handler.hub;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.telemetry.analyzer.repository.DeviceRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceRemovedEventHandler extends BaseHubEventHandler<DeviceRemovedEventAvro> {

    private final DeviceRepository repository;

    @Override
    public Class<DeviceRemovedEventAvro> getHandledEventType() {
        return DeviceRemovedEventAvro.class;
    }

    @Override
    protected DeviceRemovedEventAvro castPayload(final Object payload) {
        Objects.requireNonNull(payload, "Payload cannot be null.");
        log.debug("Casting payload{} to the class type {}.", payload, DeviceRemovedEventAvro.class);
        return (DeviceRemovedEventAvro) payload;
    }

    @Transactional
    @Override
    protected void handle(final String hubId, final DeviceRemovedEventAvro payload) {
        log.info("Removing device ID {} from the hub ID {}.", payload.getId(), hubId);
        repository.deleteByIdAndHubId(payload.getId(), hubId);
    }

}