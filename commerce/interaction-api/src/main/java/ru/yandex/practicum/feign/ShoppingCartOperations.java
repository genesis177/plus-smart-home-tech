package ru.yandex.practicum.feign;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;

/**
 * Интерфейс API клиента для управления корзиной покупок в интернет-магазине.
 */

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface ShoppingCartOperations {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    ShoppingCartDto getShoppingCart(@RequestParam @NotBlank String username);

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    ShoppingCartDto addProductToCart(
            @RequestBody @NotEmpty Map<@NotNull UUID, @NotNull @Positive Long> products,
            @RequestParam @NotBlank String username);

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    void deactivateCurrentCart(@RequestParam @NotBlank String username);

    @PutMapping("/remove")
    @ResponseStatus(HttpStatus.OK)
    ShoppingCartDto removeProductsFromCart(@RequestParam @NotBlank String username,
                                           @RequestBody Set<@NotNull UUID> products);

    @PostMapping("/{username}/add-products")
    ShoppingCartDto addProductToCart(@PathVariable String username,
                                     @RequestBody Map<UUID, Long> products);

    @PutMapping("/change-quantity")
    @ResponseStatus(HttpStatus.OK)
    ShoppingCartDto changeQuantity(@RequestParam @NotBlank String username,
                                   @RequestBody @Valid ChangeProductQuantityRequest request);
}