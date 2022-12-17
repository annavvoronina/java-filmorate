package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class Film {

    private int id;

    private String name;

    private String description;

    private LocalDate releaseDate;

    private int duration;

    private Set<Integer> listLikes = new HashSet<>();

    private Mpa mpa;

    private List<Genre> genres = new ArrayList<>();

    public void addLike(int userId) {
        listLikes.add(userId);
    }

    public void removeLike(int userId) {
        listLikes.remove(userId);
    }

}
