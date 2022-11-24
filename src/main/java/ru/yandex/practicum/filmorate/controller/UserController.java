package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getUsers() {
        return userStorage.getUsers().values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validate(user);
        log.info("Валидация пройдена");

        userStorage.create(user);
        log.info("Пользователь " + user.getLogin() + " добавлен");

        return user;
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        validate(user);
        log.info("Валидация пройдена");

        int userId = user.getId();

        if (userId != 0 && userStorage.getUsers().containsKey(userId)) {
            userStorage.update(user);
            log.info("Информация о пользователе обновлена");

            return user;
        }

        log.warn("Пользователь не найден");
        throw new ObjectNotFoundException("Нет такого пользователя");
    }

    @GetMapping(value = "/{id}")
    public User getUserById(@PathVariable int id) {
        if (userStorage.getUserById(id) == null) {
            log.warn("Пользователь не найден");
            throw new ObjectNotFoundException("Пользователь не найден");
        } else {
            return userStorage.getUserById(id);
        }
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public User addNewFriend(@PathVariable int id, @PathVariable int friendId) {
            return userService.addNewFriend(id, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public User removeFriend(@PathVariable int id, @PathVariable int friendId) {
            return userService.removeFriend(id, friendId);
    }

    @GetMapping(value = "/{id}/friends")
    public List<User> getListFriend(@PathVariable int id) {
            return userService.getFriendsList(id);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
            return userService.getCommonFriendsList(id, otherId);
    }

    private void validate(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            log.warn("Пользователь не внесен, некорректный e-mail");
            throw new ValidationException("Введен некорректный e-mail");
        }
        if (StringUtils.isBlank(user.getLogin()) || user.getLogin().contains(" ")) {
            log.warn("Пользователь не внесен, логин пустой или содержит пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (StringUtils.isEmpty(user.getName())) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Пользователь не внесен, некорректная дата рождения");
            throw new ValidationException("Дата рождения внесена некорректно");
        }
        if (!checkUniqueness(user)) {
            log.warn("Пользователь не внесен, уже есть в системе");
            throw new ValidationException("Пользователь уже есть в системе, проверьте login и e-mail");
        }
    }

    private boolean checkUniqueness(User newUser) {
        for (User user : userStorage.getUsers().values()) {
            if (!Objects.equals(user.getLogin(), newUser.getLogin()) &&
                    (Objects.equals(user.getLogin(), newUser.getLogin()) ||
                    Objects.equals(user.getEmail(), newUser.getEmail()))) {

                return false;
            }
        }

        return true;
    }

}
