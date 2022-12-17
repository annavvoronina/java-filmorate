package ru.yandex.practicum.filmorate.storage.film;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Qualifier("filmDBStorage")
public class FilmDBStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    @Autowired
    public FilmDBStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage, MpaStorage mpaStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public Map<Integer, Film> getFilms() {
        String sqlQuery = "SELECT FILM_ID, FILM_NAME, RELEASE_DATE, DESCRIPTION, DURATION, MPA_ID FROM FILM";

        List<Film> filmList = jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> buildFilmEntity(resultSet));

        return filmList.stream().collect(Collectors.toMap(Film::getId, item -> item));
    }

    @Override
    public void create(Film film) {
        String sqlQuery = "INSERT INTO FILM (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) " +
                "VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setInt(4, film.getDuration());
            preparedStatement.setInt(5, film.getMpa().getId());

            return preparedStatement;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey(), "Ключ не может быть пустым").intValue());
        if (CollectionUtils.isNotEmpty(film.getGenres())) {
            addFilmGenres(film.getId(), film.getGenres());
        }
    }

    @Override
    public void update(Film film) {
        String sqlQuery = "UPDATE FILM SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, " +
                "MPA_ID = ? WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , Date.valueOf(film.getReleaseDate())
                , film.getDuration()
                , film.getMpa().getId()
                , film.getId());
        deleteFilmGenres(film.getId());
        if (CollectionUtils.isNotEmpty(film.getGenres())) {
            addFilmGenres(film.getId(), film.getGenres());
        }
    }

    @Override
    public Film getFilmById(int id) {
        String sqlQuery = "SELECT FILM_ID, FILM_NAME, RELEASE_DATE, DESCRIPTION, DURATION, MPA_ID FROM FILM " +
                "WHERE FILM_ID = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> buildFilmEntity(rs), id)
                .stream()
                .findAny()
                .orElseThrow(() -> new ObjectNotFoundException("Фильм не найден"));
    }

    @Override
    public void addLikeFilm(int filmId, int userId) {
        String sqlQuery = "INSERT INTO LIKES (FILM_ID, USER_ID) VALUES (?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, new String[]{"LIKE_ID"});
            preparedStatement.setInt(1, filmId);
            preparedStatement.setInt(2, userId);

            return preparedStatement;
        }, keyHolder);
    }

    @Override
    public void deleteLikeFilm(int filmId, int userId) {
        String sqlQuery = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        if (jdbcTemplate.update(sqlQuery, filmId, userId) == 0) {
            throw new ObjectNotFoundException("Лайк не найден");
        }
    }

    @Override
    public List<Film> getPopularFilmList(int count) {
        String sqlQuery = "SELECT F.FILM_ID, F.FILM_NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.MPA_ID, " +
                "COUNT(L.USER_ID) FROM FILM AS F LEFT JOIN LIKES AS L ON F.FILM_ID = L.FILM_ID " +
                "GROUP BY F.FILM_ID, L.USER_ID ORDER BY L.USER_ID DESC LIMIT " + count;
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> buildFilmEntity(rs));
    }

    private void addFilmGenres(int filmId, List<Genre> genres) {
        List<Genre> genresUniq = genres.stream().distinct().collect(Collectors.toList());
        jdbcTemplate.batchUpdate(
                "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?,?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(@NotNull PreparedStatement statement, int i) throws SQLException {
                        statement.setLong(1, filmId);
                        statement.setLong(2, genresUniq.get(i).getId());
                    }

                    public int getBatchSize() {
                        return genresUniq.size();
                    }
                }
        );
    }

    private void deleteFilmGenres(int filmId) {
        String sqlQuery = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    private Film buildFilmEntity(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("FILM_ID");
        Film film = new Film();
        film.setId(id);
        film.setName(resultSet.getString("FILM_NAME"));
        film.setDescription(resultSet.getString("DESCRIPTION"));
        film.setReleaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate());
        film.setDuration(resultSet.getInt("DURATION"));
        film.setMpa(mpaStorage.getMpaById(resultSet.getInt("MPA_ID")));
        film.setGenres(genreStorage.getGenresByFilmId(id));

        return film;
    }
}
