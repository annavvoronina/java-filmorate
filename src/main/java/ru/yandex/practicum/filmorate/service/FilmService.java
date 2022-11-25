package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicationException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.time.LocalDate;
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

    public Collection<Film> getFilms() {
        return filmStorage.getFilms().values();
    }

    public void create(@Valid Film film) {
        validate(film);
        log.info("Валидация пройдена");

        filmStorage.create(film);
        log.info("Фильм " + film.getName() + " добавлен");
    }

    public void update(@Valid Film film) {
        validate(film);
        log.info("Валидация пройдена");

        int filmId = film.getId();

        if (filmId != 0 && filmStorage.getFilms().containsKey(filmId)) {
            filmStorage.update(film);
            log.info("Информация о фильме обновлена");
        } else {
            log.warn("Фильм не найден");
            throw new ObjectNotFoundException("В системе нет такого фильма");
        }
    }

    public Film getFilmById(int id) {
        if (filmStorage.getFilmById(id) == null) {
            log.warn("Фильм не найден");
            throw new ObjectNotFoundException("Фильм не найден");
        } else {
            return filmStorage.getFilmById(id);
        }
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
            throw new DuplicationException("Фильм с таким названием уже есть в системе под другим id");
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
