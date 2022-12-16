package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MPA.MPADBStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MPAStorageTests {

    private final MPADBStorage mpaStorage;

    @Test
    public void getMPAsTest() {
        List<MPA> mpa = mpaStorage.getMPAs();
        assertEquals(mpa.size(), 5);
    }

    @Test
    public void getMPAByIdTestTrue() {
        Optional<MPA> mpaOptional = Optional.ofNullable(mpaStorage.getMPAById(1));
        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void getMPAByIdTestFalse() {
        assertThrows(ObjectNotFoundException.class, () -> mpaStorage.getMPAById(8));
    }

}
