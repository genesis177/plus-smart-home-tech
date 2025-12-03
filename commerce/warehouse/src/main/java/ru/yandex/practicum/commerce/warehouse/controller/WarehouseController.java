package ru.yandex.practicum.commerce.warehouse.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.feign.WarehouseOperations;
import ru.yandex.practicum.commerce.warehouse.service.WarehouseService;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;

/**
 * REST контроллер для управления операциями на складе.
 */
@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
@Validated
@Slf4j
public class WarehouseController implements WarehouseOperations {

    private final WarehouseService warehouseService;

    /**
     * Добавляет новый продукт на склад.
     */
    @Override
    @PostMapping("/new-product")
    @ResponseStatus(HttpStatus.CREATED)
    public void addProduct(@RequestBody NewProductInWarehouseRequest product) {
        log.info("Получен запрос на добавление нового продукта с ID {} на склад", product.getProductId());
        warehouseService.addNewProduct(product);
        log.info("Продукт успешно добавлен.");
    }

    /**
     * Увеличивает количество определенного продукта на складе.
     */
    @Override
    @PostMapping("/add-product")
    @ResponseStatus(HttpStatus.CREATED)
    public void increaseProductQuantity(@RequestBody AddProductToWarehouseRequest request) {
        log.info("Получен запрос на увеличение количества продукта: {}.", request.getProductId());
        warehouseService.increaseProductQuantity(request);
        log.info("Количество продукта успешно увеличено.");
    }

    /**
     * Проверяет наличие продуктов на складе для продуктов в корзине покупок.
     *
     * @return
     */
    @Override
    @PostMapping("/check-stock")
    public BookedProductsDto checkStock(@RequestBody ShoppingCartDto shoppingCart) {
        log.info("Получен запрос на проверку наличия продуктов в корзине покупок с ID {}.",
                shoppingCart.getShoppingCartId());
        return warehouseService.checkStock(shoppingCart);
    }

    /**
     * Получает адрес склада.
     */
    @Override
    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        log.info("Получен запрос на получение адреса склада");
        return warehouseService.getAddress();
    }
}
