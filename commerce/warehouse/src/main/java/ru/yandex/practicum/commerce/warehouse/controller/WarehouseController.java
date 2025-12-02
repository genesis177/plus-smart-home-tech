package ru.yandex.practicum.commerce.warehouse.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
     *
     * @param product данные нового продукта
     */
    @Override
    public void addProduct(final NewProductInWarehouseRequest product) {
        log.info("Получен запрос на добавление нового продукта с ID {} на склад", product.getProductId());
        warehouseService.addNewProduct(product);
        log.info("Продукт успешно добавлен.");
    }

    /**
     * Увеличивает количество определенного продукта на складе.
     *
     * @param request данные запроса на увеличение количества продукта
     */
    @Override
    public void increaseProductQuantity(final AddProductToWarehouseRequest request) {
        log.info("Получен запрос на увеличение количества продукта: {}.", request.getProductId());
        warehouseService.increaseProductQuantity(request);
        log.info("Количество продукта успешно увеличено.");
    }

    /**
     * Проверяет наличие продуктов на складе для продуктов в корзине покупок.
     *
     * @param shoppingCart данные корзины покупок
     */
    @Override
    public void checkStock(final ShoppingCartDto shoppingCart) {
        log.info("Получен запрос на проверку наличия продуктов в корзине покупок с ID {}.",
                shoppingCart.getShoppingCartId());
        final BookedProductsDto bookedProducts = warehouseService.checkStock(shoppingCart);
        log.info("Возвращена общая информация о корзине покупок.");
    }

    /**
     * Получает адрес склада.
     *
     * @return адрес склада
     */
    @Override
    public AddressDto getWarehouseAddress() {
        log.info("Получен запрос на получение адреса склада");
        final AddressDto address = warehouseService.getAddress();
        log.info("Возвращен адрес из города: {}.", address.getCity());
        return address;
    }
}
