package ru.yandex.practicum.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductReturnRequest {
    @NotBlank
    UUID orderId;

    @NotBlank
    Map<UUID, Long> products;
}