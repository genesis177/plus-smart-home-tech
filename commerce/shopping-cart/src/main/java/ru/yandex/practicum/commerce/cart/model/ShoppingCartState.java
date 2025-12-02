package ru.yandex.practicum.commerce.cart.model;

/**
 * Перечисление ShoppingCartState представляет состояние корзины покупок, указывая, является ли она
 * активной (можно изменять) или деактивированной (заморожена, изменения больше не разрешены).
 */

public enum ShoppingCartState {
    ACTIVE,
    DEACTIVATED
}