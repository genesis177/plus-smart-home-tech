package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.ProductsResponseList;
import ru.yandex.practicum.dto.store.enums.ProductCategory;
import ru.yandex.practicum.dto.store.updateStockLevelStateRequest;
import ru.yandex.practicum.feign.ShoppingStoreClient;
import ru.yandex.practicum.service.StoreService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
@Slf4j
public class StoreController implements ShoppingStoreClient {
    private final StoreService storeService;

    @Override
    public ProductsResponseList getProductsByCategory(ProductCategory category, Pageable pageable) {
        log.info("Запрос на получениие товаров категории: {}", category);
        return storeService.getProductsByCategory(category, pageable);
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        log.info("Запрос на получения товара по ID: {}", productId);
        return storeService.getProductById(productId);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        log.info("Запрос на обновление товара с ID: {} и именем: {}", productDto.getProductId(),
                productDto.getProductName());
        return storeService.updateProduct(productDto);
    }

    @Override
    public Boolean removeProduct(UUID productId){
        log.info("Запрос на удаление товара с ID: {}", productId);
        return storeService.removeProduct(productId);
    }

    @Override
    public Boolean updateStockLevelState(updateStockLevelStateRequest request){
        log.info("Запрос на изменение количества товара с ID: {}", request.getProductId());
        return storeService.updateStockLevelState(request);
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        log.info("Запрос на создание товара с именем: {}",  productDto.getProductName());
        return storeService.createProduct(productDto);
    }
}
