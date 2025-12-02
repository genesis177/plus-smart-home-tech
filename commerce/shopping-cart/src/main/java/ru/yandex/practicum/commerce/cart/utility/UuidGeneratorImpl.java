package ru.yandex.practicum.commerce.cart.utility;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Реализация {@link UuidGenerator}, которая генерирует случайные UUID. Генерирует новый случайный UUID
 * и записывает сгенерированный идентификатор в журнал.
 */

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