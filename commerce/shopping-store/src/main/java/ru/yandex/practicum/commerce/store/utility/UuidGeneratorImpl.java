package ru.yandex.practicum.commerce.store.utility;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UuidGeneratorImpl implements UuidGenerator {

    @Override
    public UUID generate() {
        final UUID id = UUID.randomUUID();
        log.debug("Generated ID: {}", id);
        return id;
    }
}