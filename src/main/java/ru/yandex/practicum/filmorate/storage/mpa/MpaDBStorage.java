package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Qualifier("mpaDBStorage")
public class MpaDBStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getMpas() {
        String sqlQuery = "SELECT MPA_ID, MPA_NAME FROM MPA";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> buildMPAEntity(rs));
    }

    @Override
    public Mpa getMpaById(int id) {
        String sqlQuery = "SELECT MPA_ID, MPA_NAME FROM MPA WHERE MPA_ID = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> buildMPAEntity(rs), id)
                .stream()
                .findAny()
                .orElseThrow(() -> new ObjectNotFoundException("MPA не найден"));
    }

    private Mpa buildMPAEntity(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("MPA_ID");
        String name = resultSet.getString("MPA_NAME");
        return new Mpa(id, name);
    }
}
