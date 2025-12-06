package ru.yandex.practicum.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.feign.ShoppingCartClient;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
@Slf4j
public class ShoppingCartController implements ShoppingCartClient {
    private final ShoppingCartService shoppingCartService;

    @Override
    public ShoppingCartDto getCart(String username) {
        return shoppingCartService.getCart(username);
    }

    @Override
    public ShoppingCartDto removeFromCart(String username, Set<UUID> productIds) {
        log.info("Удаленные товары: {}, пользователь: {}", productIds, username);
        return shoppingCartService.removeFromCart(username, productIds);
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        log.info("Запрос на изменение количества товаров {}. Пользователь: {}", request, username);
        return shoppingCartService.changeProductQuantity(username, request);
    }

    @Override
    public ShoppingCartDto addProductToCart(String username, Map<UUID, Long> products) {
        log.info("Запрос на добавления товаров в корзину пользователя {}. Товары: {}", products, username);
        return shoppingCartService.addProductToCart(username, products);
    }

    @Override
    public void deleteUserCart(String username) {
        shoppingCartService.deleteUserCart(username);
    }
}
