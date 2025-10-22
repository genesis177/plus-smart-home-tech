package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.telemetry.collector.configuration.CollectorKafkaConfig;
import ru.yandex.practicum.telemetry.collector.model.enums.hub.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.hub.DeviceAddedEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.producer.KafkaEventProducer;

/**
 * Обработчик события добавления устройства от хаба.
 */

@Service
@Slf4j
public class DeviceAddedEventHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {

    public DeviceAddedEventHandler(KafkaEventProducer producer, CollectorKafkaConfig config) {
        super(producer, config);
        log.info("DeviceAddedEventHandler instantiated.");
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_ADDED;
    }

    @Override
    protected DeviceAddedEventAvro mapToAvro(final HubEvent event) {
        log.debug("Mapping DeviceAddedEvent to Avro: {}", event);
        final DeviceAddedEvent _event = (DeviceAddedEvent) event;
        final DeviceTypeAvro deviceType = DeviceTypeAvro.valueOf(_event.getDeviceType().name());
        return DeviceAddedEventAvro.newBuilder()
                .setId(_event.getId())
                .setType(deviceType)
                .build();
    }

}