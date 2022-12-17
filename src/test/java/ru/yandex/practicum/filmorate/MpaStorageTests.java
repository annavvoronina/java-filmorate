package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDBStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaStorageTests {

    private final MpaDBStorage mpaStorage;

    @Test
    public void getMpasTest() {
        List<Mpa> mpa = mpaStorage.getMpas();
        assertEquals(5, mpa.size());
    }

    @Test
    public void getMpaByIdTestTrue() {
        Optional<Mpa> mpaOptional = Optional.ofNullable(mpaStorage.getMpaById(1));
        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void getMpaByIdTestFalse() {
        ObjectNotFoundException thrown = Assertions.assertThrows(ObjectNotFoundException.class, () ->
                mpaStorage.getMpaById(8));
        Assertions.assertEquals("MPA не найден", thrown.getMessage());
    }

}
