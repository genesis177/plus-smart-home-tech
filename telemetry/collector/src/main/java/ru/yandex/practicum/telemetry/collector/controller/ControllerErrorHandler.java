package ru.yandex.practicum.telemetry.collector.controller;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.telemetry.collector.exception.ErrorResponse;

@RestControllerAdvice
@Slf4j
public class ControllerErrorHandler {

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            MissingServletRequestParameterException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(
            final RuntimeException exception) {
        return buildErrorResponse(exception, HttpStatus.BAD_REQUEST, exception.getMessage());
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGenericException(final Exception e) {
        return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(final Exception exception,
                                                             HttpStatus status, String reason) {
        log.error("{}: {}", status.value(), reason, exception);
        ErrorResponse apiError = new ErrorResponse(status, reason, LocalDateTime.now());
        return ResponseEntity.status(status).body(apiError);
    }

}