package ru.yandex.practicum.dto.product;

import java.util.Arrays;
import java.util.Optional;

/**
 * Возможные состояния степени доступности товара.
 * <p>
 * <ul>
 *   <li>{@link #ENDED} - Товар отсутствует на складе; </li>
 *   <li>{@link #FEW} - Меньше 10 единиц в наличии; </li>
 *   <li>{@link #ENOUGH} - От 10 до 100 единиц в наличии; </li>
 *   <li>{@link #MANY} - Более 100 единиц в наличии. </li>
 * </ul>
 */

public enum QuantityState {

    ENDED,
    FEW,
    ENOUGH,
    MANY;

    public static Optional<QuantityState> from(final String stringState) {
        return Arrays.stream(values())
                .filter(state -> state.name().equalsIgnoreCase(stringState))
                .findFirst();
    }

}