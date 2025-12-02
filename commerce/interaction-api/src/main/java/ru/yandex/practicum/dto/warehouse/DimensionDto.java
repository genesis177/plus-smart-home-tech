package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO, представляющий размеры товара.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DimensionDto {

    @NotNull
    @DecimalMin("1.0")
    private Double depth;

    @NotNull
    @DecimalMin("1.0")
    private Double height;

    @NotNull
    @DecimalMin("1.0")
    private Double width;

}