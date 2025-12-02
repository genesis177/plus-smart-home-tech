package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO, представляет общую информацию о забронированных товарах в корзине.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookedProductsDto {

    @NotNull
    private Double deliveryWeight;

    @NotNull
    private Double deliveryVolume;

    @NotNull
    private Boolean fragile;

}