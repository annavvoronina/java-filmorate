package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

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

    private Integer getNewFilmId() {
        if (films.keySet().isEmpty()) {
            return 1;
        }
        List<Integer> filmIdList = new ArrayList<>(films.keySet());
        return filmIdList.get(filmIdList.size() - 1) + 1;
    }

}
