package ru.yandex.practicum.dto.warehouse;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class AssemblyProductsForOrderRequest {
    UUID orderId;

    Map<UUID, Long> products;
}