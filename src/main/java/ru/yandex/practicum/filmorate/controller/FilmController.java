package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController extends BaseController {

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("Текущее количество фильмов: " + films.size());

        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validate(film);
        log.info("Валидация пройдена");

        if (!checkUniqueness(film)) {
            log.warn(film.getName() + " уже существует");
            throw new ValidationException("Фильм уже есть в системе");
        }

        Integer newFilmId = getNewFilmId();
        film.setId(newFilmId);
        films.put(newFilmId, film);
        log.info("Фильм " + film.getName() + " добавлен (id " + newFilmId + ")");

        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) throws ValidationException {
        validate(film);
        log.info("Валидация пройдена");

        Integer filmId = film.getId();

        if (filmId != 0 && films.containsKey(filmId)) {
            films.remove(filmId);
            films.put(filmId, film);
            log.info("Информация о фильме обновлена");

            return film;
        }
        log.warn("Фильм не найден");
        throw new ValidationException("В системе нет такого фильма");
    }

    private Integer getNewFilmId() {
        if (films.keySet().isEmpty()) {
            return 1;
        }

        List<Integer> filmIdList = films.keySet().stream().toList();

        return filmIdList.get(filmIdList.size() - 1) + 1;
    }

    private static void validate(@Valid @RequestBody Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            log.warn("Фильм не внесен, нет названия");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Фильм не внесен, длинное описание");
            throw new ValidationException("Максимальная длина описания - 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Фильм не внесен, дата релиза внесена некорректно");
            throw new ValidationException("Дата релиза внесена некорректно");
        }
        if (film.getDuration() <= 0) {
            log.warn("Фильм не внесен, продолжительность фильма должна быть положительной");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }

    }

    private boolean checkUniqueness(Film newFilm) {
        for (Film film : films.values()) {
            if (film.getName().equals(newFilm.getName()) && film.getReleaseDate().equals(newFilm.getReleaseDate())) {
                return false;
            }
        }

        return true;
    }

}
