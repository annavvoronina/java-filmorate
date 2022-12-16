package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Qualifier("userDBStorage")
public class UserDBStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Integer, User> getUsers() {
        String sqlQuery = "SELECT USER_ID, EMAIL, LOGIN, USER_NAME, BIRTHDAY " +
                "FROM USERS";

        List<User> userList = jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> buildUserEntity(resultSet));

        return userList.stream().collect(Collectors.toMap(User::getId, item -> item));
    }

    @Override
    public void create(User user) {
        String sqlQuery = "INSERT INTO USERS (EMAIL, LOGIN, USER_NAME, BIRTHDAY) " +
                "VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getLogin());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setDate(4, Date.valueOf(user.getBirthday()));

            return preparedStatement;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
    }

    @Override
    public void update(User user) {
        String sqlQuery = "UPDATE USERS " +
                "SET EMAIL = ?, LOGIN = ?, USER_NAME = ?, BIRTHDAY = ? " +
                "WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery
                , user.getEmail()
                , user.getLogin()
                , user.getName()
                , user.getBirthday()
                , user.getId());
    }

    @Override
    public User getUserById(int id) {
        String sqlQuery = "SELECT USER_ID, EMAIL, LOGIN, USER_NAME, BIRTHDAY " +
                "FROM USERS " +
                "WHERE USER_ID = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> buildUserEntity(rs), id)
                .stream()
                .findAny()
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
    }

    @Override
    public void addNewFriend(int user1Id, int user2Id, FriendshipStatus status) {
        String sqlQuery = "INSERT INTO FRIENDSHIP (USER1_ID, USER2_ID, STATUS) values (?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, new String[]{"FRIENDSHIP_ID"});
            preparedStatement.setInt(1, user1Id);
            preparedStatement.setInt(2, user2Id);
            preparedStatement.setString(3, String.valueOf(status));
            return preparedStatement;
        }, keyHolder);
        if (status.equals(FriendshipStatus.CONFIRMED)) {
            String sqlQuery1 = "UPDATE FRIENDSHIP SET USER1_ID = ?, USER2_ID = ?, STATUS = ?";
            jdbcTemplate.update(sqlQuery1
                    , user2Id
                    , user1Id
                    , status);
        }
    }

    @Override
    public void removeFriend(int user1Id, int user2Id) {
        String sqlQuery = "DELETE FROM FRIENDSHIP WHERE USER1_ID = ? AND USER2_ID = ?";
        jdbcTemplate.update(sqlQuery, user1Id, user2Id);
    }

    @Override
    public List<User> getFriendsList(int userId) {
        String sqlQuery = "SELECT users2.USER_ID, users2.EMAIL, users2.LOGIN, users2.USER_NAME, users2.BIRTHDAY "
                + "FROM USERS AS users1 "
                + "JOIN FRIENDSHIP AS friend ON users1.USER_ID = friend.USER1_ID "
                + "JOIN USERS AS users2 ON friend.USER2_ID = users2.USER_ID "
                + "WHERE users1.USER_ID = " + userId;
        return jdbcTemplate.query(sqlQuery,
                (resultSet, rowNum) -> buildUserEntity(resultSet));
    }

    @Override
    public List<User> getCommonFriendsList(int user1Id, int user2Id) {
        String sqlQuery = "SELECT f1.USER2_ID, u.USER_ID, u.EMAIL, u.LOGIN, u.USER_NAME, u.BIRTHDAY "
                + "FROM FRIENDSHIP AS f1, FRIENDSHIP AS f2 "
                + "JOIN USERS AS u ON u.USER_ID = f1.USER2_ID "
                + "WHERE f1.USER2_ID = f2.USER2_ID AND f1.USER1_ID = " + user1Id + " AND f2.USER1_ID = " + user2Id;
        return jdbcTemplate.query(sqlQuery,
                (resultSet, rowNum) -> buildUserEntity(resultSet));
    }

    private User buildUserEntity(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("USER_ID");
        String email = resultSet.getString("EMAIL");
        String login = resultSet.getString("LOGIN");
        String userName = resultSet.getString("USER_NAME");
        LocalDate birthday = resultSet.getDate("BIRTHDAY").toLocalDate();

        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setLogin(login);
        user.setName(userName);
        user.setBirthday(birthday);

        return user;
    }

}
