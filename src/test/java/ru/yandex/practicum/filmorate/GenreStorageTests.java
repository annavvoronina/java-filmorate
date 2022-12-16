package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDBStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreStorageTests {

    private final GenreDBStorage genreStorage;

    @Test
    public void getGenresTest() {
        List<Genre> genre = genreStorage.getGenres();
        assertEquals(genre.size(), 6);
    }

    @Test
    public void getGenreByIdTestTrue() {
        Optional<Genre> genreOptional = Optional.ofNullable(genreStorage.getGenreById(1));
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void getGenreByIdTestFalse() {
        assertThrows(ObjectNotFoundException.class, () -> genreStorage.getGenreById(8));
    }

}
