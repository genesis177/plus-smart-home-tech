package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ShoppingCartService {
    ShoppingCartDto addProductToCart(String username, Map<UUID, Long> products);

    ShoppingCartDto getCart(String username);

    ShoppingCartDto removeFromCart(String username, Set<UUID> productIds);

    void deleteUserCart(String username);

    ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request);
}
