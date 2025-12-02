package ru.yandex.practicum.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Определяет причины бизнес-исключений.
 */

@Getter
@AllArgsConstructor
public enum ExceptionReason {

    PRODUCT_NOT_FOUND("Такого товара нет в магазине.", HttpStatus.NOT_FOUND),
    NOT_AUTHORIZED_USER("Имя пользователя должно быть указано.", HttpStatus.UNAUTHORIZED),
    NO_PRODUCTS_IN_SHOPPING_CART("В корзине нет товаров.", HttpStatus.BAD_REQUEST),
    SHOPPING_CART_MODIFICATION_NOT_ALLOWED("Невозможно изменить деактивированную корзину.", HttpStatus.FORBIDDEN),
    SPECIFIED_PRODUCT_ALREADY_IN_WAREHOUSE("Товар с этим описанием уже зарегистрирован на складе.", HttpStatus.BAD_REQUEST),
    PRODUCT_IN_SHOPPING_CART_LOW_QUANTITY_IN_WAREHOUSE("Товара из корзины нет в требуемом количестве на складе.", HttpStatus.BAD_REQUEST),
    NO_SPECIFIED_PRODUCT_IN_WAREHOUSE("Нет информации о товаре на складе.", HttpStatus.BAD_REQUEST);


    private final String code;
    private final String message;
    private final HttpStatus status;

    ExceptionReason(final String message, final HttpStatus status) {
        this.code = this.name();
        ;
        this.message = message;
        this.status = status;
    }

}