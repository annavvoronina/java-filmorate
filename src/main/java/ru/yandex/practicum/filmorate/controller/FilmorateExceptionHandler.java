package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.Response;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@RestControllerAdvice
public class FilmorateExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    protected ResponseEntity<Response> handleException(ValidationException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response(exception.getMessage()));
    }

}
