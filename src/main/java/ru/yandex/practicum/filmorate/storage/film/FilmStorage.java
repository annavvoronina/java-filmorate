package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {

    Map<Integer, Film> getFilms();

    void create(Film film);

    void update(Film film);

    Film getFilmById(int id);

    void addLikeFilm(int id, int userId);

    void deleteLikeFilm(int id, int userId);

    List<Film> getPopularFilmList(int count);

}
