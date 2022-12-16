package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.MPA.MPADBStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDBStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDBStorage;
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
public class FilmStorageTests {

    private final FilmDBStorage filmStorage;
    private final UserDBStorage userStorage;
    private final MPADBStorage mpaStorage;
    private final GenreDBStorage genreStorage;


    @Test
    void getFilmByIdTestTrue() {
        Film newFilm = new Film();
        newFilm.setName("nisi eiusmod");
        newFilm.setDescription("adipisicing");
        newFilm.setReleaseDate(LocalDate.parse("1967-03-25"));
        newFilm.setDuration(100);
        newFilm.setMpa(mpaStorage.getMPAById(1));
        newFilm.setGenres(genreStorage.getGenresByFilmId(2));
        filmStorage.create(newFilm);

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    void getFilmByIdTestFalse() {
        assertThrows(ObjectNotFoundException.class, () -> filmStorage.getFilmById(6));
    }

    @Test
    public void getFilmsTest() {
        Film newFilm1 = new Film();
        newFilm1.setName("nisi eiusmod");
        newFilm1.setDescription("adipisicing");
        newFilm1.setReleaseDate(LocalDate.parse("1967-03-25"));
        newFilm1.setDuration(100);
        newFilm1.setMpa(mpaStorage.getMPAById(1));
        newFilm1.setGenres(genreStorage.getGenresByFilmId(2));
        filmStorage.create(newFilm1);

        Film newFilm2 = new Film();
        newFilm2.setName("nisi eiusmod 2");
        newFilm2.setDescription("adipisicing 2");
        newFilm2.setReleaseDate(LocalDate.parse("1967-03-26"));
        newFilm2.setDuration(102);
        newFilm2.setMpa(mpaStorage.getMPAById(3));
        newFilm2.setGenres(genreStorage.getGenresByFilmId(4));
        filmStorage.create(newFilm2);

        Map<Integer, Film> films = filmStorage.getFilms();
        assertEquals(films.size(), 2);
    }

    @Test
    public void getPopularFilmListTest() {
        Film newFilm1 = new Film();
        newFilm1.setName("nisi eiusmod");
        newFilm1.setDescription("adipisicing");
        newFilm1.setReleaseDate(LocalDate.parse("1967-03-25"));
        newFilm1.setDuration(100);
        newFilm1.setMpa(mpaStorage.getMPAById(1));
        newFilm1.setGenres(genreStorage.getGenresByFilmId(2));
        filmStorage.create(newFilm1);

        Film newFilm2 = new Film();
        newFilm2.setName("nisi eiusmod 2");
        newFilm2.setDescription("adipisicing 2");
        newFilm2.setReleaseDate(LocalDate.parse("1967-03-26"));
        newFilm2.setDuration(102);
        newFilm2.setMpa(mpaStorage.getMPAById(3));
        newFilm2.setGenres(genreStorage.getGenresByFilmId(4));
        filmStorage.create(newFilm2);

        User newUser = new User();
        newUser.setEmail("mail@mail.ru");
        newUser.setLogin("dolore");
        newUser.setName("Nick Name");
        newUser.setBirthday(LocalDate.parse("1946-08-20"));
        userStorage.create(newUser);

        List<Film> popularFilmList = filmStorage.getPopularFilmList(10);
        assertEquals(popularFilmList.size(), 2);

        filmStorage.addLikeFilm(1, 1);

        List<Film> popularFilmList2 = filmStorage.getPopularFilmList(1);
        assertEquals(popularFilmList2.size(), 1);
        assertEquals(popularFilmList2.get(0).getName(), "nisi eiusmod");
    }

}
