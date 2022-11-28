package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.constraints.Positive;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        filmService.create(film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        filmService.update(film);
        return film;
    }

    @GetMapping(value = "/{id}")
    public Film getFilmById(@PathVariable int id) {
        return filmService.getFilmById(id);
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

}
