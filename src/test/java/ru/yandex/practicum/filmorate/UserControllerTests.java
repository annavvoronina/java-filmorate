package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateUser() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                                .contentType("application/json;charset=UTF-8")
                                .content("{" +
                                        "\"login\": \"dolore\"," +
                                        "\"name\": \"Nick Name\"," +
                                        "\"email\": \"mail@mail.ru\"," +
                                        "\"birthday\": \"1946-08-20\"" +
                                        "}")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
        mockMvc.perform(get("/users").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
    }

    @Test
    public void testCreateUserFailLogin() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                                .contentType("application/json;charset=UTF-8")
                                .content("{" +
                                        "\"login\": \"dolore ullamco\"," +
                                        "\"name\": \"Nick Name\"," +
                                        "\"email\": \"yandex@mail.ru\"," +
                                        "\"birthday\": \"2446-08-20\"" +
                                        "}")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateUserFailEmail() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                                .contentType("application/json;charset=UTF-8")
                                .content("{" +
                                        "\"login\": \"doloreullamco\"," +
                                        "\"name\": \"Nick Name\"," +
                                        "\"email\": \"mail.ru\"," +
                                        "\"birthday\": \"2446-08-20\"" +
                                        "}")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateUserEmptyName() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                                .contentType("application/json;charset=UTF-8")
                                .content("{" +
                                        "\"login\": \"common\"," +
                                        "\"email\": \"test1@mail.ru\"," +
                                        "\"birthday\": \"2000-08-20\"" +
                                        "}")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
        mockMvc.perform(get("/users").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
    }

    @Test
    public void testCreateUserFailBirthday() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                                .contentType("application/json;charset=UTF-8")
                                .content("{" +
                                        "\"login\": \"doloreulla\"," +
                                        "\"name\": \"Nick Name\"," +
                                        "\"email\": \"test1@mail.ru\"," +
                                        "\"birthday\": \"2446-08-20\"" +
                                        "}")
                )
                .andExpect(status().isBadRequest());
    }

}
