package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDBStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserStorageTests {

    private final UserDBStorage userStorage;

    @Test
    void getUserByIdTestTrue() {
        User newUser = new User();
        newUser.setEmail("mail@mail.ru");
        newUser.setLogin("dolore");
        newUser.setName("Nick Name");
        newUser.setBirthday(LocalDate.parse("1946-08-20"));
        userStorage.create(newUser);

        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    void getUserByIdTestFalse() {
        assertThrows(ObjectNotFoundException.class, () -> userStorage.getUserById(80));
    }

    @Test
    void getUsersTest() {
        User newUser1 = new User();
        newUser1.setEmail("mail@mail.ru");
        newUser1.setLogin("dolore");
        newUser1.setName("Nick Name");
        newUser1.setBirthday(LocalDate.parse("1946-08-20"));
        userStorage.create(newUser1);

        User newUser2 = new User();
        newUser2.setEmail("mail2@mail.ru");
        newUser2.setLogin("dolore2");
        newUser2.setName("Nick Name 2");
        newUser2.setBirthday(LocalDate.parse("1946-08-22"));
        userStorage.create(newUser2);

        Map<Integer, User> users = userStorage.getUsers();
        assertEquals(users.size(), 2);
    }

    @Test
    void getFriendsListTest() {
        User newUser1 = new User();
        newUser1.setEmail("mail@mail.ru");
        newUser1.setLogin("dolore");
        newUser1.setName("Nick Name");
        newUser1.setBirthday(LocalDate.parse("1946-08-20"));
        userStorage.create(newUser1);

        User newUser2 = new User();
        newUser2.setEmail("mail2@mail.ru");
        newUser2.setLogin("dolore2");
        newUser2.setName("Nick Name 2");
        newUser2.setBirthday(LocalDate.parse("1946-08-22"));
        userStorage.create(newUser2);

        userStorage.addNewFriend(newUser1.getId(), newUser2.getId(), FriendshipStatus.UNCONFIRMED);
        List<User> listFriend = userStorage.getFriendsList(newUser1.getId());
        assertEquals(listFriend.size(), 1);
        userStorage.removeFriend(newUser1.getId(), newUser2.getId());
        listFriend = userStorage.getFriendsList(newUser1.getId());
        assertEquals(listFriend.size(), 0);
    }

    @Test
    void getCommonFriendsListTest() {
        User newUser1 = new User();
        newUser1.setEmail("mail@mail.ru");
        newUser1.setLogin("dolore");
        newUser1.setName("Nick Name");
        newUser1.setBirthday(LocalDate.parse("1946-08-20"));
        userStorage.create(newUser1);

        User newUser2 = new User();
        newUser2.setEmail("mail2@mail.ru");
        newUser2.setLogin("dolore2");
        newUser2.setName("Nick Name 2");
        newUser2.setBirthday(LocalDate.parse("1946-08-22"));
        userStorage.create(newUser2);

        User newUser3 = new User();
        newUser3.setEmail("mail3@mail.ru");
        newUser3.setLogin("dolore3");
        newUser3.setName("Nick Name 3");
        newUser3.setBirthday(LocalDate.parse("1946-08-23"));
        userStorage.create(newUser3);

        userStorage.addNewFriend(newUser1.getId(), newUser3.getId(), FriendshipStatus.UNCONFIRMED);
        userStorage.addNewFriend(newUser2.getId(), newUser3.getId(), FriendshipStatus.UNCONFIRMED);

        List<User> listCommonFriends = userStorage.getCommonFriendsList(newUser1.getId(), newUser2.getId());
        assertEquals(listCommonFriends.size(), 1);
        assertEquals(listCommonFriends.get(0).getName(), "Nick Name 3");
    }

}
