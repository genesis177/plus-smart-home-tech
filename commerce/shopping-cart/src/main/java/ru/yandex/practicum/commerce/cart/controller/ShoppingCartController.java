package ru.yandex.practicum.commerce.cart.controller;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.feign.ShoppingCartOperations;
import ru.yandex.practicum.commerce.cart.service.ShoppingCartService;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;

@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
@Slf4j
@Validated
public abstract class ShoppingCartController implements ShoppingCartOperations {

    private final ShoppingCartService shoppingCartService;

    @GetMapping("/{username}")
    @Override
    public ShoppingCartDto getShoppingCart(@PathVariable String username) {
        return shoppingCartService.getShoppingCart(username);
    }


    @PostMapping("/{username}/add-products")
    @Override
    public ShoppingCartDto addProductToCart(@PathVariable String username,
                                            @RequestBody Map<UUID, Long> products) {
        return shoppingCartService.addProductsToCart(username, products);
    }

    @PostMapping("/{username}/change-quantity")
    @Override
    public ShoppingCartDto changeQuantity(@PathVariable String username,
                                          @RequestBody ChangeProductQuantityRequest request) {
        return shoppingCartService.changeProductQuantity(username, request);
    }

    @PostMapping("/{username}/remove-products")
    @Override
    public ShoppingCartDto removeProductsFromCart(@PathVariable String username,
                                                  @RequestBody Set<UUID> products) {
        return shoppingCartService.retainProductsInTheCart(username, products);
    }

    @PostMapping("/{username}/deactivate")
    @Override
    public void deactivateCurrentCart(@PathVariable String username) {
        shoppingCartService.deactivateShoppingCart(username);
    }
}