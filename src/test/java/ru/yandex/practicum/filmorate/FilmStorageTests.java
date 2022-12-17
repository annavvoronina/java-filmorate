package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDBStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDBStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDBStorage;
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
public class FilmStorageTests {

    private final FilmDBStorage filmStorage;
    private final UserDBStorage userStorage;
    private final MpaDBStorage mpaStorage;
    private final GenreDBStorage genreStorage;

    private final Film newFilm1 = new Film();
    private final Film newFilm2 = new Film();
    private final User newUser = new User();

    @BeforeEach
    public void init() {
        newFilm1.setName("nisi eiusmod");
        newFilm1.setDescription("adipisicing");
        newFilm1.setReleaseDate(LocalDate.parse("1967-03-25"));
        newFilm1.setDuration(100);
        newFilm1.setMpa(mpaStorage.getMpaById(1));
        newFilm1.setGenres(genreStorage.getGenresByFilmId(2));

        newFilm2.setName("nisi eiusmod 2");
        newFilm2.setDescription("adipisicing 2");
        newFilm2.setReleaseDate(LocalDate.parse("1967-03-26"));
        newFilm2.setDuration(102);
        newFilm2.setMpa(mpaStorage.getMpaById(3));
        newFilm2.setGenres(genreStorage.getGenresByFilmId(4));

        newUser.setEmail("mail@mail.ru");
        newUser.setLogin("dolore");
        newUser.setName("Nick Name");
        newUser.setBirthday(LocalDate.parse("1946-08-20"));
    }

    @Test
    void getFilmByIdTestTrue() {
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    void getFilmByIdTestFalse() {
        ObjectNotFoundException thrown = Assertions.assertThrows(ObjectNotFoundException.class, () ->
                filmStorage.getFilmById(80));
        Assertions.assertEquals("Фильм не найден", thrown.getMessage());
    }

    @Test
    public void getFilmsTest() {
        filmStorage.create(newFilm1);
        filmStorage.create(newFilm2);
        Map<Integer, Film> films = filmStorage.getFilms();
        assertEquals(2, films.size());
    }

    @Test
    public void getPopularFilmListTest() {
        userStorage.create(newUser);
        List<Film> popularFilmList = filmStorage.getPopularFilmList(10);
        assertEquals(2, popularFilmList.size());

        filmStorage.addLikeFilm(1, 1);

        List<Film> popularFilmList2 = filmStorage.getPopularFilmList(1);
        assertEquals(1, popularFilmList2.size());
        assertEquals("nisi eiusmod", popularFilmList2.get(0).getName());
    }

}
