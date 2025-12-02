package ru.yandex.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;

/**
 * Интерфейс API клиента для операций со складом интернет-магазина.
 */

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseOperations {

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    void addProduct(@Validated @RequestBody NewProductInWarehouseRequest product);

    @PutMapping("/check")
    @ResponseStatus(HttpStatus.OK)
    void checkStock(@Validated @RequestBody ShoppingCartDto shoppingCart);

    @PutMapping("/add")
    @ResponseStatus(HttpStatus.OK)
    void increaseProductQuantity(@Validated @RequestBody AddProductToWarehouseRequest request);

    @GetMapping("/address")
    @ResponseStatus(HttpStatus.OK)
    AddressDto getWarehouseAddress();

}