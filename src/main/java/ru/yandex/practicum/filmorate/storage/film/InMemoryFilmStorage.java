package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmComparator;

import java.util.*;

@Component
@Qualifier("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

    private final Comparator<Film> filmComparator = new FilmComparator();

    @Override
    public Map<Integer, Film> getFilms() {
        return films;
    }

    @Override
    public void create(Film film) {
        Integer newFilmId = getNewFilmId();
        film.setId(newFilmId);
        films.put(newFilmId, film);
    }

    @Override
    public void update(Film film) {
        Integer filmId = film.getId();
        films.remove(filmId);
        films.put(filmId, film);
    }

    @Override
    public Film getFilmById(int id) {
        return films.get(id);
    }

    @Override
    public void addLikeFilm(int id, int userId) {
        Film film = getFilmById(id);
        film.addLike(userId);
    }

    @Override
    public void deleteLikeFilm(int id, int userId) {
        Film film = getFilmById(id);
        film.removeLike(userId);
    }

    @Override
    public List<Film> getPopularFilmList(int count) {
        Set<Film> sortFilmList = new TreeSet<>(filmComparator);
        sortFilmList.addAll(getFilms().values());
        List<Film> popularFilmList = new ArrayList<>();
        for (Film film : sortFilmList) {
            if (popularFilmList.size() < count) {
                popularFilmList.add(film);
            }
        }
        return popularFilmList;
    }

    private Integer getNewFilmId() {
        if (films.keySet().isEmpty()) {
            return 1;
        }
        List<Integer> filmIdList = new ArrayList<>(films.keySet());
        return filmIdList.get(filmIdList.size() - 1) + 1;
    }

}
