package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
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

import java.util.List;

import static hexlet.code.config.SpringConfigForTest.TEST_PROFILE;
import static hexlet.code.controller.TaskStatusController.ID;
import static hexlet.code.controller.TaskStatusController.STATUS_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.TEST_EMAIL;
import static hexlet.code.utils.TestUtils.TEST_STATUS;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    public void beforeEach() throws Exception {
        utils.regDefaultUser();
    }

    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    public void createStatusTest() throws Exception {
        utils.addDefaultStatus()
                .andExpect(status().isCreated());

        assertEquals(1, taskStatusRepository.count());

        final TaskStatus taskStatus = taskStatusRepository.findAll().get(0);
        assertEquals(taskStatus.getName(), TEST_STATUS);
    }

    @Test
    public void createInvalidStatusTest() throws Exception {
        TaskStatusDto invalidStatus = new TaskStatusDto();
        final var request = post(STATUS_CONTROLLER_PATH)
                .content(asJson(invalidStatus))
                .contentType(APPLICATION_JSON);

        utils.perform(request, TEST_EMAIL)
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void getStatusByIdTest() throws Exception {
        utils.addDefaultStatus();
        assertEquals(1, taskStatusRepository.count());

        final Long id = taskStatusRepository.findAll().get(0).getId();
        final var request = get(STATUS_CONTROLLER_PATH + ID, id);
        final var response = utils.perform(request, TEST_EMAIL)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final TaskStatus taskStatus = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(taskStatus.getName(), TEST_STATUS);
    }

    @Test
    public void getAllStatusTest() throws Exception {
        utils.addDefaultStatus();
        assertEquals(1, taskStatusRepository.count());

        final var request = get(STATUS_CONTROLLER_PATH);
        final var response = utils.perform(request, TEST_EMAIL)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final List<TaskStatus> statuses = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(statuses.size(), 1);
    }

    @Test
    public void updateStatusTest() throws Exception {
        utils.addDefaultStatus()
                .andExpect(status().isCreated());
        assertEquals(1, taskStatusRepository.count());

        final Long id = taskStatusRepository.findAll().get(0).getId();
        final TaskStatusDto expectStatusDto = new TaskStatusDto("newStatus");
        final var requestToUpdate = put(STATUS_CONTROLLER_PATH + ID, id)
                .content(asJson(expectStatusDto))
                .contentType(APPLICATION_JSON);

        utils.perform(requestToUpdate, TEST_EMAIL)
                .andExpect(status().isOk());

        final TaskStatus updatedStatus = taskStatusRepository.findAll().get(0);
        assertEquals(updatedStatus.getName(), expectStatusDto.getName());
    }

    @Test
    public void deleteStatusTest() throws Exception {
        utils.addDefaultStatus()
                .andExpect(status().isCreated());
        assertEquals(1, taskStatusRepository.count());

        final Long id = taskStatusRepository.findAll().get(0).getId();
        final var request = delete(STATUS_CONTROLLER_PATH + ID, id);
        utils.perform(request, TEST_EMAIL)
                .andExpect(status().isOk());

        assertEquals(0, taskStatusRepository.count());
    }

    @Test
    public void deleteWhenStatusAssociatedWithTaskTest() throws Exception {
        utils.addDefaultLabel();
        utils.addDefaultStatus();
        utils.addDefaultTask();

        final Long statusId = taskStatusRepository.findAll().get(0).getId();
        final var request = delete(STATUS_CONTROLLER_PATH + ID, statusId);
        utils.perform(request, TEST_EMAIL)
                .andExpect(status().isUnprocessableEntity());

    }

}
