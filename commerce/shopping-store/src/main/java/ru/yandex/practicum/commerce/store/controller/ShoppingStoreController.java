package ru.yandex.practicum.commerce.store.controller;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.store.service.ShoppingStoreService;
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
        final SetProductQuantityStateRequest request = new SetProductQuantityStateRequest(productId,
                quantityState);
        log.info("Получен запрос на обновление состояния количества для продукта с ID {}.",
                request.getProductId());
        boolean isUpdated = shoppingStoreService.updateQuantityState(request);
        log.info("Состояние количества продукта успешно обновлено.");
        return isUpdated;
    }

    /**
     * Удаляет продукт из ассортимента магазина. (Функция для сотрудников управления)
     *
     * @param productId ID продукта, который нужно удалить
     * @return true, если продукт был успешно удален
     */
    @Override
    @PutMapping("/removeProductFromStore")
    @ResponseStatus(HttpStatus.OK)
    public boolean removeProductFromStore(final UUID productId) {
        log.info("Получен запрос на удаление продукта с ID {} из магазина.", productId);
        boolean isRemoved = shoppingStoreService.removeProduct(productId);
        log.info("Состояние продукта успешно обновлено на 'DEACTIVATE'.");
        return isRemoved;
    }
}
