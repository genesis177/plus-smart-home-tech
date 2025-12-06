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

    @NotNull(message = "Product ID must not be null")
    UUID productId;

    Boolean fragile;

    @NotNull(message = "Dimension must not be null")
    DimensionDto dimension;

    @NotNull(message = "Weight must not be null")
    @Min(value = 1, message = "Weight must be greater than 0")
    Double weight;
}
