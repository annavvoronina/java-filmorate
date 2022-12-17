package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
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

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserStorageTests {

    private final UserDBStorage userStorage;

    private final User newUser1 = new User();
    private final User newUser2 = new User();
    private final User newUser3 = new User();

    @BeforeEach
    public void init() {
        newUser1.setEmail("mail1@mail.ru");
        newUser1.setLogin("dolore1");
        newUser1.setName("Nick Name 1");
        newUser1.setBirthday(LocalDate.parse("1946-08-21"));

        newUser2.setEmail("mail2@mail.ru");
        newUser2.setLogin("dolore2");
        newUser2.setName("Nick Name 2");
        newUser2.setBirthday(LocalDate.parse("1946-08-22"));

        newUser3.setEmail("mail3@mail.ru");
        newUser3.setLogin("dolore3");
        newUser3.setName("Nick Name 3");
        newUser3.setBirthday(LocalDate.parse("1946-08-23"));
    }

    @Test
    void getUserByIdTestTrue() {
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    void getUserByIdTestFalse() {
        ObjectNotFoundException thrown = Assertions.assertThrows(ObjectNotFoundException.class, () ->
                userStorage.getUserById(80));
        Assertions.assertEquals("Пользователь не найден", thrown.getMessage());
    }

    @Test
    void getUsersTest() {
        Map<Integer, User> users = userStorage.getUsers();
        assertEquals(3, users.size());
    }

    @Test
    void getFriendsListTest() {
        userStorage.addNewFriend(1, 2, FriendshipStatus.UNCONFIRMED);
        List<User> listFriend = userStorage.getFriendsList(1);
        assertEquals(1, listFriend.size());
        userStorage.removeFriend(1, 2);
        listFriend = userStorage.getFriendsList(1);
        assertEquals(0, listFriend.size());
    }

    @Test
    void getCommonFriendsListTest() {
        userStorage.create(newUser1);
        userStorage.create(newUser2);
        userStorage.create(newUser3);

        userStorage.addNewFriend(1, 3, FriendshipStatus.UNCONFIRMED);
        userStorage.addNewFriend(2, 3, FriendshipStatus.UNCONFIRMED);
        List<User> listCommonFriends = userStorage.getCommonFriendsList(1, 2);
        assertEquals(1, listCommonFriends.size());
        assertEquals("Nick Name 3", listCommonFriends.get(0).getName());

        userStorage.removeFriend(1, 3);
        userStorage.removeFriend(2, 3);
    }

}
