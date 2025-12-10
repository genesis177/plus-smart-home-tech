package ru.yandex.practicum.feign;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.ProductsResponseList;
import ru.yandex.practicum.dto.store.enums.ProductCategory;
import ru.yandex.practicum.dto.store.updateStockLevelStateRequest;

import java.util.UUID;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreClient {
    @GetMapping
    ProductsResponseList getProductsByCategory(@RequestParam ProductCategory category,
                                               @Valid Pageable pageable) throws FeignException;

    @GetMapping("/{productId}")
    ProductDto getProductById(@PathVariable UUID productId) throws FeignException;

    @PostMapping
    ProductDto updateProduct(@Valid @RequestBody ProductDto productDto) throws FeignException;

    @PostMapping("/removeProductFromStore")
    Boolean removeProduct(@RequestBody UUID productId) throws FeignException;

    @PostMapping("/quantityState")
    Boolean updateStockLevelState(@Valid updateStockLevelStateRequest request) throws FeignException;

    @PutMapping
    ProductDto createProduct(@Valid @RequestBody ProductDto productDto) throws FeignException;

}
