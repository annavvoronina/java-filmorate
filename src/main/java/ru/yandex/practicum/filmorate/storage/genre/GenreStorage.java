package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    List<Genre> getGenres();

    Genre getGenreById(int id);

    List<Genre> getGenresByFilmId(int filmId);

    void createGenres(int filmId, List<Genre> genres);

    void deleteGenres(int filmId);
}
