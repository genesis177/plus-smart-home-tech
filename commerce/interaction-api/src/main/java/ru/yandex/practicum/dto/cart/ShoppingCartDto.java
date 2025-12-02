package ru.yandex.practicum.dto.cart;

import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Представляет корзину покупок в интернет-магазине.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingCartDto {

    /**
     * Уникальный идентификатор корзины покупок в базе данных.
     */
    @NotNull
    private UUID shoppingCartId;

    /**
     * Соответствие идентификаторов товаров их выбранному количеству.
     */
    @NotNull
    private Map<UUID, Long> products;


}