package ru.yandex.practicum.handler.hub.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.handler.hub.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.repository.SensorRepository;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceRemovedHandler implements HubEventHandler {
    final SensorRepository sensorRepository;

    @Override
    public String getType() {
        return DeviceRemovedHandler.class.getSimpleName();
    }

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        DeviceRemovedEventAvro deviceRemovedAvro = (DeviceRemovedEventAvro) event.getPayload();
        sensorRepository.deleteByIdAndHubId(deviceRemovedAvro.getId(), event.getHubId());
        log.info("Device removed: {}", deviceRemovedAvro);
    }
}