package ru.yandex.practicum.dto.payment;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDto {
    UUID paymentId;
    Double totalPayment;
    Double deliveryTotal;
    Double feeTotal;
}