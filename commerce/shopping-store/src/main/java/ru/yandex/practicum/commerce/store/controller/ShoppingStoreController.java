package ru.yandex.practicum.commerce.store.controller;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.store.service.ShoppingStoreService;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.feign.ShoppingStoreOperations;
import ru.yandex.practicum.dto.product.ProductCategory;
import ru.yandex.practicum.dto.product.ProductDto;
import ru.yandex.practicum.dto.product.ProductPage;
import ru.yandex.practicum.dto.product.QuantityState;
import ru.yandex.practicum.dto.product.SetProductQuantityStateRequest;

/**
 * REST контроллер для управления продуктами в магазине.
 */
@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ShoppingStoreController implements ShoppingStoreOperations {

    private final ShoppingStoreService shoppingStoreService;

    /**
     * Получает список продуктов с пагинацией, отфильтрованный по конкретной категории.
     *
     * @param category категория продукта
     * @param pageable параметры пагинации
     * @return страница с продуктами
     */
    @Override
    public ProductPage getProductsByCategory(final ProductCategory category,
                                             final Pageable pageable) {
        log.info("Получен запрос на получение продуктов для категории: {}, с пагинацией: {}.",
                category, pageable);
        final Page<ProductDto> response = shoppingStoreService.getProductsByCategory(category,
                pageable);
        log.info("Возвращен список продуктов размером: {}", response.getTotalElements());
        return new ProductPage(response);
    }

    /**
     * Получает информацию о продукте по его ID.
     *
     * @param productId ID продукта
     * @return информация о продукте
     */
    @Override
    public ProductDto getProductById(final UUID productId) {
        log.info("Получен запрос на получение данных о продукте с ID: {}.", productId);
        final ProductDto product = shoppingStoreService.getProductById(productId);
        log.info("Возвращены данные о продукте {}.", product.getProductName());
        return product;
    }

    /**
     * Создает новый продукт в магазине.
     *
     * @param productDto данные о продукте
     * @return созданный продукт
     */
    @Override
    public ProductDto addProduct(final ProductDto productDto) {
        log.info("Получен запрос на добавление нового продукта: {}.", productDto.getProductName());
        final ProductDto savedProduct = shoppingStoreService.addProduct(productDto);
        log.info("Продукт {} сохранен с ID {}.", savedProduct.getProductName(),
                savedProduct.getProductId());
        return savedProduct;
    }

    /**
     * Обновляет существующий продукт.
     *
     * @param productDto обновленные данные о продукте
     * @return обновленный продукт
     */
    @Override
    public ProductDto updateProduct(final ProductDto productDto) {
        log.info("Получен запрос на обновление продукта с ID {}.", productDto.getProductId());
        final ProductDto updatedProduct = shoppingStoreService.updateProduct(productDto);
        log.info("Продукт с ID {} успешно обновлен.", updatedProduct.getProductId());
        return updatedProduct;
    }

    /**
     * Обновляет состояние количества продукта в магазине. (API вызывается со стороны склада)
     *
     * @param productId     уникальный идентификатор продукта
     * @param quantityState новое состояние количества продукта
     * @return true, если обновление прошло успешно
     */
    @Override
    public boolean updateQuantityState(final UUID productId, final QuantityState quantityState) {
        try {
            SetProductQuantityStateRequest request = new SetProductQuantityStateRequest(productId, quantityState);
            boolean result = shoppingStoreService.updateQuantityState(request);
            return result; // 200 или 201
        } catch (Exception e) {
            throw e; // чтобы глобальный обработчик вернул 500
        }
    }

    @Override
    @PutMapping("/removeProductFromStore")
    @ResponseStatus(HttpStatus.OK)
    public boolean removeProductFromStore(final UUID productId) {
        try {
            return shoppingStoreService.removeProduct(productId); // при успехе 200
        } catch (ProductNotFoundException e) {
            throw e; // обработчик глобальный
        } catch (Exception e) {
            log.error("Ошибка при удалении продукта: ", e);
            throw new RuntimeException(e); // вызовет 500
        }
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Void> handleProductNotFound(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleAnyException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500
    }
}
