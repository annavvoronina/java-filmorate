package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController extends BaseController {

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

        if (!checkUniqueness(user)) {
            log.warn(user.getLogin() + " уже существует");

            throw new ValidationException("Пользователь уже есть в системе, проверьте login и e-mail");
        }

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

        List<Integer> userIdList = users.keySet().stream().toList();

        return userIdList.get(userIdList.size() - 1) + 1;
    }

    public static void validate(@Valid @RequestBody User user) {
        if (!user.getEmail().contains("@")) {
            log.warn("Пользователь не внесен, некорректный e-mail");
            throw new ValidationException("Введен некорректный e-mail");
        }
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.warn("Пользователь не внесен, логин пустой или содержит пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Пользователь не внесен, некорректная дата рождения");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    public boolean checkUniqueness(User newUser) {
        for (User user : users.values()) {
            if (user.getLogin().equals(newUser.getLogin()) || user.getEmail().equals(newUser.getEmail())) {
                return false;
            }
        }

        return true;
    }

}
