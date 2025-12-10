package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewProductInWarehouseRequest {

    @NotNull(message = "Идентификатор продукта не должен быть null")
    UUID productId;

    Boolean fragile;

    @NotNull(message = "Габариты не должны быть null")
    DimensionDto dimension;

    @NotNull(message = "Вес не должен быть null")
    @Min(value = 1, message = "Вес должен быть больше 0")
    Double weight;
}