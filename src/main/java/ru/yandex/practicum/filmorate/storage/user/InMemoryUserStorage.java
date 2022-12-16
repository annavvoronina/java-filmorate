package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Map<Integer, User> getUsers() {
        return users;
    }

    @Override
    public void create(User user) {
        Integer newUserId = getNewUserId();
        user.setId(newUserId);
        users.put(newUserId, user);
    }

    @Override
    public void update(User user) {
        Integer userId = user.getId();
        users.remove(userId);
        users.put(userId, user);
    }

    @Override
    public User getUserById(int id) {
        return users.get(id);
    }

    @Override
    public void addNewFriend(int user1Id, int user2Id, FriendshipStatus status) {
        User user = getUserById(user1Id);
        user.addFriend(user2Id);
        User friend = getUserById(user2Id);
        friend.addFriend(user1Id);
    }

    @Override
    public void removeFriend(int user1Id, int user2Id) {
        User user = getUserById(user1Id);
        user.removeFriend(user2Id);
        User friend = getUserById(user2Id);
        friend.removeFriend(user1Id);
    }

    @Override
    public List<User> getFriendsList(int userId) {
        User user = getUserById(userId);
        List<User> userFriends = new ArrayList<>();
        for (int friendId : user.getFriends()) {
            userFriends.add(getUserById(friendId));
        }
        return userFriends;
    }

    @Override
    public List<User> getCommonFriendsList(int user1Id, int user2Id) {
        List<User> commonFriendsList = new ArrayList<>();
        for (int firstUserFriendId : getUserById(user1Id).getFriends()) {
            for (int secondUserFriendId : getUserById(user2Id).getFriends()) {
                if (Objects.equals(firstUserFriendId, secondUserFriendId)) {
                    commonFriendsList.add(getUserById(firstUserFriendId));
                }
            }
        }
        return commonFriendsList;
    }

    private Integer getNewUserId() {
        if (users.keySet().isEmpty()) {
            return 1;
        }
        List<Integer> userIdList = new ArrayList<>(users.keySet());
        return userIdList.get(userIdList.size() - 1) + 1;
    }

}
