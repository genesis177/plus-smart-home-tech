package ru.yandex.practicum.exception.policy;

import org.springframework.http.HttpStatus;

/**
 * Контракт для пользовательских исключений, обеспечивающий стандартизированное поведение по всему API.
 * Предоставляет некоторые из необходимых свойств для схемы ответа об ошибке.
 */
public interface ExceptionPolicy {

    /**
     * @return код ошибки, специфичный для бизнеса
     */
    String getCode();

    /**
     * @return понятное для пользователя сообщение об ошибке
     */
    String getMessage();

    /**
     * @return HTTP статус-код, связанный с исключением
     */
    HttpStatus getHttpStatus();
}
