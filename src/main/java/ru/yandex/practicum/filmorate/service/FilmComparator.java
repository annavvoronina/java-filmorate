package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;

public class FilmComparator implements Comparator<Film> {

    @Override
    public int compare(Film film1, Film film2) {
        if (film1.getListLikes().size() == film2.getListLikes().size()) {
            return Integer.compare(film1.getId(), film2.getId());
        } else {
            return Integer.compare(film2.getListLikes().size(), film1.getListLikes().size());
        }
    }

}
