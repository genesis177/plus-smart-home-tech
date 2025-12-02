package ru.yandex.practicum.commerce.warehouse.service;

import ru.yandex.practicum.dto.warehouse.AddressDto;

/**
 * Интерфейс сервиса для получения адреса склада.
 */

public interface WarehouseAddressService {

    AddressDto getAddress();
}