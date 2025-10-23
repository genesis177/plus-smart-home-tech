package ru.yandex.practicum.telemetry.collector.controller;

import jakarta.validation.Valid;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.telemetry.collector.model.enums.hub.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.enums.SensorEventType;
import ru.yandex.practicum.telemetry.collector.service.handler.hub.HubEventHandler;
import ru.yandex.practicum.telemetry.collector.service.handler.sensor.SensorEventHandler;

@RestController
@RequestMapping("/events")
@Slf4j
@Validated
public class EventController {

    private final Map<SensorEventType, SensorEventHandler> sensorEventHandlers;

    private final Map<HubEventType, HubEventHandler> hubEventHandlers;

    @Autowired
    public EventController(final Set<SensorEventHandler> sensorEventHandlers,
                           final Set<HubEventHandler> hubEventHandlers) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getMessageType, Function.identity()));
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
    }


    @PostMapping("/sensors")
    @ResponseStatus(HttpStatus.OK)
    public void collectSensorEvent(@Valid @RequestBody final SensorEvent event) {
        log.info("Received sensor event of type: {}.", event.getType());
        if (sensorEventHandlers.containsKey(event.getType())) {
            sensorEventHandlers.get(event.getType()).handle(event);
        } else {
            log.warn("No handler found for sensor event type: {}.", event.getType());
            throw new IllegalArgumentException(
                    "Handler for the event type " + event.getType() + " not found.");
        }
        log.info("Successfully handled sensor event of type: {}.", event.getType());
    }

    @PostMapping("/hubs")
    @ResponseStatus(HttpStatus.OK)
    public void collectHubEvent(@Valid @RequestBody final HubEvent event) {
        log.info("Received hub event of type: {}.", event.getType());
        if (hubEventHandlers.containsKey(event.getType())) {
            hubEventHandlers.get(event.getType()).handle(event);
        } else {
            log.warn("No handler found for hub event type: {}.", event.getType());
            throw new IllegalArgumentException(
                    "Handler for the event type " + event.getType() + " not found.");
        }
        log.info("Successfully handled hub event of type: {}.", event.getType());
    }

}