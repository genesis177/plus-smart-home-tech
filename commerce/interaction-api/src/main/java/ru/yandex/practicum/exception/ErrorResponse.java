package ru.yandex.practicum.exception;

import lombok.Data;

@Data
public class ErrorResponse {
    private final String error;
    private final String message;
}
