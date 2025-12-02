package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Запрос на увеличение количества товара по его идентификатору.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddProductToWarehouseRequest {

    @NotNull
    private UUID productId;

    @NotNull
    @Min(1)
    private Long quantity;

}