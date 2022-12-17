package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Slf4j
@Service
public class GenreService {

    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre getGenreById(int id) throws ObjectNotFoundException {
        if (genreStorage.getGenreById(id) == null) {
            log.warn("Жанр не найден");
            throw new ObjectNotFoundException("Жанр не найден");
        } else {
            return genreStorage.getGenreById(id);
        }
    }

    public List<Genre> getGenres() {
        return genreStorage.getGenres();
    }

}
