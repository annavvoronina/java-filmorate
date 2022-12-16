package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicationException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDBStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers().values();
    }

    public User create(User user) {
        validate(user);
        log.info("Валидация пройдена");

        userStorage.create(user);
        log.info("Пользователь " + user.getLogin() + " добавлен");

        return userStorage.getUserById(user.getId());
    }

    public User put(User user) {
        validate(user);
        log.info("Валидация пройдена");

        int userId = user.getId();

        if (userId != 0 && userStorage.getUsers().containsKey(userId)) {
            userStorage.update(user);
            log.info("Информация о пользователе обновлена");
        } else {
            log.warn("Пользователь не найден");
            throw new ObjectNotFoundException("Нет такого пользователя");
        }

        return userStorage.getUserById(user.getId());
    }

    public User getUserById(int id) {
        if (userStorage.getUserById(id) == null) {
            log.warn("Пользователь не найден");
            throw new ObjectNotFoundException("Пользователь не найден");
        } else {
            return userStorage.getUserById(id);
        }
    }

    public User addNewFriend(int id, int friendId) {
        if (userStorage.getUserById(id) == null || userStorage.getUserById(friendId) == null) {
            log.warn("Пользователь не найден");
            throw new ObjectNotFoundException("Как минимум один из пользователей не найден");
        } else {
            User user = userStorage.getUserById(id);
            List<User> friendList = getFriendsList(friendId);
            FriendshipStatus status = friendList.contains(user)
                    ? FriendshipStatus.CONFIRMED : FriendshipStatus.UNCONFIRMED;

            userStorage.addNewFriend(id, friendId, status);

            return userStorage.getUserById(id);
        }
    }

    public User removeFriend(int id, int friendId) {
        if (id == friendId ||
                userStorage.getUserById(id) == null ||
                userStorage.getUserById(friendId) == null) {
            log.warn("Пользователь не найден");
            throw new ObjectNotFoundException("Как минимум один из пользователей не найден");
        } else {
            userStorage.removeFriend(id, friendId);

            return userStorage.getUserById(id);
        }
    }

    public List<User> getFriendsList(int id) {
        if (userStorage.getUserById(id) == null) {
            log.warn("Пользователь не найден");
            throw new ObjectNotFoundException("Пользователь не найден");
        } else {
            return userStorage.getFriendsList(id);
        }
    }

    public List<User> getCommonFriendsList(int firstUserId, int secondUserId) {
        if (userStorage.getUserById(firstUserId) == null || userStorage.getUserById(secondUserId) == null) {
            log.warn("Пользователь не найден");
            throw new ObjectNotFoundException("Как минимум один из пользователей не найден");
        } else {
            return userStorage.getCommonFriendsList(firstUserId, secondUserId);
        }
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
            throw new DuplicationException("Пользователь уже есть в системе, проверьте login и e-mail");
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