package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        log.info("Текущее количество пользователей: " + users.size());

        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validate(user);
        log.info("Валидация пройдена");

        Integer newUserId = getNewUserId();
        user.setId(newUserId);
        users.put(newUserId, user);
        log.info("Пользователь " + user.getLogin() + " добавлен (id " + newUserId + ")");

        return user;
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        validate(user);
        log.info("Валидация пройдена");

        Integer userId = user.getId();

        if (userId != 0 && users.containsKey(userId)) {
            users.remove(userId);
            users.put(userId, user);
            log.info("Информация о пользователе обновлена");

            return user;
        }
        log.warn(user.getName() + " не найден");
        throw new ValidationException("Нет такого пользователя");
    }

    private Integer getNewUserId() {
        if (users.keySet().isEmpty()) {
            return 1;
        }

        List<Integer> userIdList = new ArrayList<>(users.keySet());

        return userIdList.get(userIdList.size() - 1) + 1;
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
        if (user.getName() == null) {
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
        for (User user : users.values()) {
            if (!Objects.equals(user.getLogin(), newUser.getLogin()) &&
                    (Objects.equals(user.getLogin(), newUser.getLogin()) ||
                    Objects.equals(user.getEmail(), newUser.getEmail()))) {

                return false;
            }
        }

        return true;
    }
}
