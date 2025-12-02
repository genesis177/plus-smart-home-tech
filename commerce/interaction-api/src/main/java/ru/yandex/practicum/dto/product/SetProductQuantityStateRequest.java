package ru.yandex.practicum.dto.product;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Запрос на изменение состояния количества товара.
 */
@Data
@AllArgsConstructor
public class SetProductQuantityStateRequest {

    @NotNull
    private UUID productId;

    @NotNull
    private QuantityState quantityState;
}