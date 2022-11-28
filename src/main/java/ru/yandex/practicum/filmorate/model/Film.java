package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
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

    public void addLike(int userId) {
        listLikes.add(userId);
    }

    public void removeLike(int userId) {
        listLikes.remove(userId);
    }

}
