package ru.yandex.practicum.dto.order;

public enum OrderState {
    NEW,
    DONE,
    CANCELED,
    PAID,
    ON_PAYMENT,
    ON_DELIVERY,
    DELIVERED,
    ASSEMBLED,
    COMPLETED,
    DELIVERY_FAILED,
    ASSEMBLY_FAILED,
    PAYMENT_FAILED,
    PRODUCT_RETURNED
}