package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForTest;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static hexlet.code.config.SpringConfigForTest.TEST_PROFILE;
import static hexlet.code.controller.UserController.ID;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.TEST_EMAIL;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForTest.class)
public class UserControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestUtils utils;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    public void registration() throws Exception {
        assertEquals(0, userRepository.count());
        utils.regDefaultUser();
        assertEquals(1, userRepository.count());
    }

    @Test
    public void getUserById() throws Exception {

        utils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);
        final var response = utils.perform(get(USER_CONTROLLER_PATH + ID, expectedUser.getId()),
                        expectedUser.getEmail())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final User user = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(expectedUser.getId()).isEqualTo(user.getId());
        assertThat(expectedUser.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(expectedUser.getLastName()).isEqualTo(user.getLastName());
        assertThat(expectedUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void getAllUsers() throws Exception {
        utils.regDefaultUser();

        final var response = utils.perform(get(USER_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<User> users = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(users).hasSize(1);
    }

    @Test
    public void updateUser() throws Exception {
        utils.regDefaultUser();
        final Long id = userRepository.findByEmail(TEST_EMAIL).get().getId();

        UserDto expectDto = new UserDto(
                "update@mail.ru",
                "updateFName",
                "updateLName",
                "updatePass");

        final var request = put(USER_CONTROLLER_PATH + ID, id)
                .content(asJson(expectDto))
                .contentType(APPLICATION_JSON);

        utils.perform(request, TEST_EMAIL).andExpect(status().isOk());

        final User actualUser = userRepository.findById(id).get();
        assertThat(actualUser.getFirstName()).isEqualTo(expectDto.getFirstName());
        assertThat(actualUser.getLastName()).isEqualTo(expectDto.getLastName());
        assertThat(actualUser.getEmail()).isEqualTo(expectDto.getEmail());
        assertEquals(1, userRepository.count());
    }

    @Test
    public void deleteUser() throws Exception {
        utils.regDefaultUser();
        final Long id = userRepository.findByEmail(TEST_EMAIL).get().getId();

        assertEquals(1, userRepository.count());

        final var request = delete(USER_CONTROLLER_PATH + ID, id);
        utils.perform(request, TEST_EMAIL).andExpect(status().isOk());

        assertEquals(0, userRepository.count());
    }
}
