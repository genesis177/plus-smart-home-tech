package ru.yandex.practicum.dto.delivery;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.warehouse.AddressDto;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class DeliveryDto {
    UUID deliveryId;

    @NotBlank
    AddressDto fromAddress;

    @NotBlank
    AddressDto toAddress;

    @NotBlank
    UUID orderId;

    @NotBlank
    DeliveryState deliveryState;

    @NotBlank
    Double weight;

    @NotBlank
    Double volume;

    @NotBlank
    boolean fragile;
}