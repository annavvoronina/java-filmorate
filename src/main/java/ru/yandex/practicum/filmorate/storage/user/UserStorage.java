package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserStorage {

    Map<Integer, User> getUsers();

    void create(User user);

    void update(User user);

    User getUserById(int id);

}
