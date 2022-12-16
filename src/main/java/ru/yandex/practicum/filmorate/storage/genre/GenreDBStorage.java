package ru.yandex.practicum.filmorate.storage.genre;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Qualifier("genreDBStorage")
public class GenreDBStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getGenres() {
        String sqlQuery = "SELECT GENRE_ID, GENRE_NAME " +
                "FROM GENRE";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> buildGenreEntity(rs));
    }

    @Override
    public Genre getGenreById(int id) {
        String sqlQuery = "SELECT GENRE_ID, GENRE_NAME " +
                "FROM GENRE " +
                "WHERE GENRE_ID = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> buildGenreEntity(rs), id)
                .stream()
                .findAny()
                .orElseThrow(() -> new ObjectNotFoundException("Жанр не найден"));
    }

    @Override
    public List<Genre> getGenresByFilmId(int filmId) throws ObjectNotFoundException {
        String sqlQuery = "SELECT GENRE.GENRE_ID, GENRE.GENRE_NAME " +
                "FROM FILM_GENRE " +
                "JOIN GENRE ON FILM_GENRE.GENRE_ID = GENRE.GENRE_ID  " +
                "WHERE FILM_GENRE.FILM_ID = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> buildGenreEntity(rs), filmId);
    }

    @Override
    public void createGenres(int filmId, List<Genre> genres) {
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

    @Override
    public void deleteGenres(int filmId) {
        String sqlQuery = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    private Genre buildGenreEntity(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("GENRE_ID");
        String name = resultSet.getString("GENRE_NAME");

        return new Genre(id, name);
    }
}
