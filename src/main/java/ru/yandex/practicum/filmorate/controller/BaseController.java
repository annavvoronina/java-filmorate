package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@RestControllerAdvice
public class BaseController {

    @ExceptionHandler({ ValidationException.class })
    protected ResponseEntity<RuntimeException> handleException(RuntimeException exception) {
        exception.setStackTrace(new StackTraceElement[0]);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception);
    }

}
