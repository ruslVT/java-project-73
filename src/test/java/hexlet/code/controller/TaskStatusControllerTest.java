package hexlet.code.controller;

import hexlet.code.config.SpringConfigForTest;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static hexlet.code.config.SpringConfigForTest.TEST_PROFILE;
import static hexlet.code.controller.TaskStatusController.ID;
import static hexlet.code.controller.TaskStatusController.STATUS_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.TEST_EMAIL;
import static hexlet.code.utils.TestUtils.TEST_STATUS;
import static hexlet.code.utils.TestUtils.asJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForTest.class)
public class TaskStatusControllerTest {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestUtils utils;

    @BeforeEach
    public void regUser() throws Exception {
        utils.regDefaultUser();
    }

    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    public void createStatus() throws Exception {
        utils.addDefaultStatus()
                .andExpect(status().isCreated());

        assertEquals(1, taskStatusRepository.count());

        final TaskStatus taskStatus = taskStatusRepository.findAll().get(0);
        assertEquals(taskStatus.getName(), TEST_STATUS);
    }

    @Test
    public void updateStatus() throws Exception {
        utils.addDefaultStatus()
                .andExpect(status().isCreated());
        assertEquals(1, taskStatusRepository.count());

        final TaskStatus addedStatus = taskStatusRepository.findAll().get(0);
        final TaskStatusDto statusDtoForUpdate = new TaskStatusDto("newStatus");
        final var requestToUpdate = put(STATUS_CONTROLLER_PATH + ID, addedStatus.getId())
                .content(asJson(statusDtoForUpdate))
                .contentType(APPLICATION_JSON);

        utils.perform(requestToUpdate, TEST_EMAIL)
                .andExpect(status().isOk());

        final TaskStatus updatedStatus = taskStatusRepository.findAll().get(0);
        assertEquals(updatedStatus.getName(), statusDtoForUpdate.getName());
    }

    @Test
    public void deleteStatus() throws Exception {
        utils.addDefaultStatus()
                .andExpect(status().isCreated());
        assertEquals(1, taskStatusRepository.count());

        final Long id = taskStatusRepository.findAll().get(0).getId();
        final var request = delete(STATUS_CONTROLLER_PATH + ID, id);
        utils.perform(request, TEST_EMAIL)
                .andExpect(status().isOk());

        assertEquals(0, taskStatusRepository.count());
    }


}
