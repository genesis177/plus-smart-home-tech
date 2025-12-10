package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.model.Product;

@Mapper(componentModel = "spring")
public interface ShoppingStoreMapper {
    Product toEntity(ProductDto productDto);

    ProductDto toDto(Product product);
}
