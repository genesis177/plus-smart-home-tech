package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Представляет запрос на добавление нового товара на склад.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewProductInWarehouseRequest {

    @NotNull
    private UUID productId;

    private Boolean fragile;

    @NotNull
    @Valid
    private DimensionDto dimension;

    @NotNull
    @DecimalMin(value = "1.0")
    private Double weight;

}