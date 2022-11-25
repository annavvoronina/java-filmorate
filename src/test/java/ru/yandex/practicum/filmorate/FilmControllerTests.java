package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
class FilmControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext applicationContext;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public FilmStorage filmStorage() {
            return new InMemoryFilmStorage();
        }

        @Bean
        public UserStorage userStorage() {
            return new InMemoryUserStorage();
        }

        @Bean
        public FilmService filmService() {
            return new FilmService(filmStorage(), userStorage());
        }
    }

    @Test
    public void testCreateFilm() throws Exception {
        mockMvc.perform(
                        post("/films")
                                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                                .contentType("application/json;charset=UTF-8")
                                .content("{" +
                                        "\"name\": \"nisi eiusmod\"," +
                                        "\"description\": \"adipisicing\"," +
                                        "\"releaseDate\": \"1967-03-25\"," +
                                        "\"duration\": 100" +
                                        "}")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
        mockMvc.perform(get("/films").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));
    }

    @Test
    public void testCreateFilmFailName() throws Exception {
        mockMvc.perform(
                        post("/films")
                                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                                .contentType("application/json;charset=UTF-8")
                                .content("{" +
                                        "\"name\": \"\"," +
                                        "\"description\": \"Description\"," +
                                        "\"releaseDate\": \"1900-03-25\"," +
                                        "\"duration\": 200" +
                                        "}")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateFilmFailDescription() throws Exception {
        mockMvc.perform(
                        post("/films")
                                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                                .contentType("application/json;charset=UTF-8")
                                .content("{" +
                                        "\"name\": \"Film name\"," +
                                        "\"description\": \"Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.\"," +
                                        "\"releaseDate\": \"1900-03-25\"," +
                                        "\"duration\": 200" +
                                        "}")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateFilmFailReleaseDate() throws Exception {
        mockMvc.perform(
                        post("/films")
                                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                                .contentType("application/json;charset=UTF-8")
                                .content("{" +
                                        "\"name\": \"Name\"," +
                                        "\"description\": \"Description\"," +
                                        "\"releaseDate\": \"1890-03-25\"," +
                                        "\"duration\": 200" +
                                        "}")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateFilmFailDuration() throws Exception {
        mockMvc.perform(
                        post("/films")
                                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                                .contentType("application/json;charset=UTF-8")
                                .content("{" +
                                        "\"name\": \"Name\"," +
                                        "\"description\": \"Description\"," +
                                        "\"releaseDate\": \"1890-03-25\"," +
                                        "\"duration\": -200" +
                                        "}")
                )
                .andExpect(status().isBadRequest());
    }

}
