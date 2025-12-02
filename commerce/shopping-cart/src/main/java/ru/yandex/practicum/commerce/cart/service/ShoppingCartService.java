package ru.yandex.practicum.commerce.cart.service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;

/**
 * Интерфейс сервиса для управления товарами в корзине покупок.
 */

public interface ShoppingCartService {

    ShoppingCartDto getShoppingCart(String username);

    ShoppingCartDto addProductsToCart(String username, Map<UUID, Long> products);

    void deactivateShoppingCart(String username);

    ShoppingCartDto retainProductsInTheCart(String username, Set<UUID> products);

    ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request);
}