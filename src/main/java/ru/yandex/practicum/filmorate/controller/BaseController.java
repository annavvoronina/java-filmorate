package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import ru.yandex.practicum.filmorate.exception.ValidationException;

public class BaseController {

    @ExceptionHandler({ ValidationException.class })
    protected ResponseEntity<RuntimeException> handleException(RuntimeException exception) {
        exception.setStackTrace(new StackTraceElement[0]);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception);
    }

}
