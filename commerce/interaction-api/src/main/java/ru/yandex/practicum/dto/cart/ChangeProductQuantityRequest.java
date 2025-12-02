package ru.yandex.practicum.dto.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Объект запроса для изменения количества товара в корзине.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeProductQuantityRequest {

    @NotNull
    private UUID productId;

    @NotNull
    @Positive
    private Long newQuantity;

}