package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {

    Map<Integer, User> getUsers();

    void create(User user);

    void update(User user);

    User getUserById(int id);

    void addNewFriend(int user1Id, int user2Id, FriendshipStatus status);

    void removeFriend(int id, int friendId);

    List<User> getFriendsList(int id);

    List<User> getCommonFriendsList(int id, int secondId);
}
