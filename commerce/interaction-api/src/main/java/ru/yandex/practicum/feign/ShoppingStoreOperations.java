package ru.yandex.practicum.feign;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.dto.product.ProductCategory;
import ru.yandex.practicum.dto.product.ProductDto;
import ru.yandex.practicum.dto.product.ProductPage;
import ru.yandex.practicum.dto.product.QuantityState;

/**
 * Интерфейс API клиента для управления товарами в интернет-магазине.
 */

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreOperations {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    ProductPage getProductsByCategory(
            @RequestParam("category") ProductCategory category,
            @PageableDefault(page = 0, size = 10, sort = "productName") Pageable pageable);


    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    ProductDto getProductById(@PathVariable("productId") UUID productId);


    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    ProductDto addProduct(@Validated @RequestBody ProductDto productDto);


    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    ProductDto updateProduct(@Validated @RequestBody ProductDto productDto);

    @PutMapping("/quantityState")
    @ResponseStatus(HttpStatus.OK)
    boolean updateQuantityState(@RequestParam("productId") @NotNull UUID productId,
                                @RequestParam("quantityState") @NotNull QuantityState quantityState);


    @PutMapping("/removeProductFromStore")
    @ResponseStatus(HttpStatus.OK)
    boolean removeProductFromStore(@RequestBody UUID productId);
}