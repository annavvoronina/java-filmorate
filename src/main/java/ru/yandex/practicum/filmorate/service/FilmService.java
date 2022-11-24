package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final Comparator<Film> filmComparator = new FilmComparator();


    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addLikeFilm(int id, int userId) {
        if (filmStorage.getFilmById(id) == null || userStorage.getUserById(userId) == null) {
            log.warn("Фильм или пользователь не найден");
            throw new ObjectNotFoundException("Фильм или пользователь не найден");
        } else {
            Film film = filmStorage.getFilmById(id);
            film.addLike(userId);

            return film;
        }
    }

    public Film deleteLikeFilm(int id, int userId) {
        if (filmStorage.getFilmById(id) == null || userStorage.getUserById(userId) == null) {
            log.warn("Фильм или пользователь не найден");
            throw new ObjectNotFoundException("Фильм или пользователь не найден");
        } else {
            Film film = filmStorage.getFilmById(id);
            film.removeLike(userId);

            return film;
        }
    }

    public List<Film> getPopularFilmList(int count) {
        Set<Film> sortFilmList = new TreeSet<>(filmComparator);
        sortFilmList.addAll(filmStorage.getFilms().values());
        List<Film> popularFilmList = new ArrayList<>();
        for (Film film : sortFilmList) {
            if (popularFilmList.size() < count) {
                popularFilmList.add(film);
            }
        }
        return popularFilmList;
    }

}
