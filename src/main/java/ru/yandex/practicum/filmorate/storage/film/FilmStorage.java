package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {

    Map<Integer, Film> getFilms();

    void create(Film film);

    void update(Film film);

    Film getFilmById(int id);

}
