package ru.yandex.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.ProductsResponseList;
import ru.yandex.practicum.dto.store.enums.ProductCategory;
import ru.yandex.practicum.dto.store.updateStockLevelStateRequest;

import java.util.UUID;

public interface StoreService {
    ProductDto getProductById(UUID productId);

    ProductsResponseList getProductsByCategory(ProductCategory category, Pageable pageable);

    ProductDto createProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    Boolean removeProduct(UUID productId);

    Boolean updateStockLevelState(updateStockLevelStateRequest request);

}
