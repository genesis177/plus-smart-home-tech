package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {
    AddressDto getWarehouseAddress();

    void addProductToWarehouse(AddProductToWarehouseRequest request);

    BookedProductsDto checkProductQuantity(ShoppingCartDto shoppingCart);

    void createNewProductInWarehouse(NewProductInWarehouseRequest newProductInWarehouseRequest);

    void shipToDelivery(ShippedToDeliveryRequest request);

    void acceptReturn(Map<UUID, Long> returnedProducts);

    BookedProductsDto assembleProducts(AssemblyProductsForOrderRequest request);



}