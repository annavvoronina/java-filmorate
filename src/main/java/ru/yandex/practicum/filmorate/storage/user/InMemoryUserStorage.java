package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
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

    private Integer getNewUserId() {
        if (users.keySet().isEmpty()) {
            return 1;
        }
        List<Integer> userIdList = new ArrayList<>(users.keySet());
        return userIdList.get(userIdList.size() - 1) + 1;
    }

}
