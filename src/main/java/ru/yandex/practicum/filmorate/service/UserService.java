package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addNewFriend (int id, int friendId) {
        if (userStorage.getUserById(id) == null || userStorage.getUserById(friendId) == null) {
            log.warn("Пользователь не найден");
            throw new ObjectNotFoundException("Как минимум один из пользователей не найден");
        } else {
            User user = userStorage.getUserById(id);
            user.addFriend(friendId);
            User friend = userStorage.getUserById(friendId);
            friend.addFriend(id);

            return user;
        }
    }

    public User removeFriend (int id, int friendId) {
        if (id == friendId ||
                userStorage.getUserById(id) == null ||
                userStorage.getUserById(friendId) == null) {
            log.warn("Пользователь не найден");
            throw new ObjectNotFoundException("Как минимум один из пользователей не найден");
        } else {
            User user = userStorage.getUserById(id);
            user.removeFriend(friendId);
            User friend = userStorage.getUserById(friendId);
            friend.removeFriend(id);

            return user;
        }
    }

    public List<User> getFriendsList(int id) {
        if (userStorage.getUserById(id) == null) {
            log.warn("Пользователь не найден");
            throw new ObjectNotFoundException("Пользователь не найден");
        } else {
            User user = userStorage.getUserById(id);
            List<User> userFriends = new ArrayList<>();
            for (int friendId : user.getFriends()) {
                userFriends.add(userStorage.getUserById(friendId));
            }
            return userFriends;
        }
    }

    public List<User> getCommonFriendsList(int firstUserId, int secondUserId) {
        if (userStorage.getUserById(firstUserId) == null || userStorage.getUserById(secondUserId) == null) {
            log.warn("Пользователь не найден");
            throw new ObjectNotFoundException("Как минимум один из пользователей не найден");
        } else {
            List<User> commonFriendsList = new ArrayList<>();
            for (int firstUserFriendId : userStorage.getUserById(firstUserId).getFriends()) {
                for (int secondUserFriendId : userStorage.getUserById(secondUserId).getFriends()) {
                    if (Objects.equals(firstUserFriendId, secondUserFriendId)) {
                        commonFriendsList.add(userStorage.getUserById(firstUserFriendId));
                    }
                }
            }
            return commonFriendsList;
        }
    }

}