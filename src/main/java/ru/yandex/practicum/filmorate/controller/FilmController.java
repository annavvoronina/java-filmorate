package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmStorage.getFilms().values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validate(film);
        log.info("Валидация пройдена");

        filmStorage.create(film);
        log.info("Фильм " + film.getName() + " добавлен");

        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        validate(film);
        log.info("Валидация пройдена");

        int filmId = film.getId();

        if (filmId != 0 && filmStorage.getFilms().containsKey(filmId)) {
            filmStorage.update(film);
            log.info("Информация о фильме обновлена");

            return film;
        }

        log.warn("Фильм не найден");
        throw new ObjectNotFoundException("В системе нет такого фильма");
    }

    @GetMapping(value = "/{id}")
    public Film getFilmById(@PathVariable int id) {
        if (filmStorage.getFilmById(id) == null) {
            log.warn("Фильм не найден");
            throw new ObjectNotFoundException("Фильм не найден");
        } else {
            return filmStorage.getFilmById(id);
        }
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public Film addLikeFilm(@PathVariable int id, @PathVariable int userId) {
        return filmService.addLikeFilm(id, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public Film deleteLikeFilm(@PathVariable int id, @PathVariable int userId) {
        return filmService.deleteLikeFilm(id, userId);
    }

    @GetMapping(value = "/popular")
    public List<Film> getPopularFilmList(@RequestParam(defaultValue = "10", required = false) @Positive int count) {
        return filmService.getPopularFilmList(count);
    }

    private void validate(Film film) {
        if (StringUtils.isBlank(film.getName())) {
            log.warn("Фильм не внесен, нет названия");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.warn("Фильм не внесен, длинное описание");
            throw new ValidationException("Максимальная длина описания - 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Фильм не внесен, дата релиза внесена некорректно");
            throw new ValidationException("Дата релиза внесена некорректно");
        }
        if (film.getDuration() <= 0) {
            log.warn("Фильм не внесен, продолжительность фильма должна быть положительной");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        if (!checkUniqueness(film)) {
            log.warn("Фильм не внесен, уже есть в системе");
            throw new ValidationException("Фильм с таким названием уже есть в системе под другим id");
        }
    }

    private boolean checkUniqueness(Film newFilm) {
        for (Film film : filmStorage.getFilms().values()) {
            if (!Objects.equals(film.getId(), newFilm.getId()) &&
                    Objects.equals(film.getName(), newFilm.getName()) &&
                    Objects.equals(film.getReleaseDate(), newFilm.getReleaseDate())) {

                return false;
            }
        }

        return true;
    }

}
