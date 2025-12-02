package ru.yandex.practicum.dto.product;

/**
 * Возможные состояния товара.
 * <p>
 * <ul>
 *   <li>{@link #ACTIVE} - Товар доступен для покупки и видим для клиентов; </li>
 *   <li>{@link #DEACTIVATE} - Товар больше не доступен для покупки, но остается в системе для исторических записей.</li>
 * </ul>
 */

public enum ProductState {

    ACTIVE,
    DEACTIVATE

}