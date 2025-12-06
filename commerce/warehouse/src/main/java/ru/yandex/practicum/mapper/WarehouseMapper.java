package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.model.Warehouse;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    Warehouse toEntity(NewProductInWarehouseRequest request);
}
