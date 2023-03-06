package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForTest;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static hexlet.code.config.SpringConfigForTest.TEST_PROFILE;
import static hexlet.code.controller.TaskController.ID;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.TEST_DESCRIPTION;
import static hexlet.code.utils.TestUtils.TEST_EMAIL;
import static hexlet.code.utils.TestUtils.TEST_FIRST_NAME;
import static hexlet.code.utils.TestUtils.TEST_LABEL_NAME;
import static hexlet.code.utils.TestUtils.TEST_STATUS;
import static hexlet.code.utils.TestUtils.TEST_TASK_NAME;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
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
public class TaskControllerTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TestUtils utils;

    @BeforeEach
    public void beforeEach() throws Exception {
        utils.regDefaultUser();
        utils.addDefaultStatus();
        utils.addDefaultLabel();
    }

    @AfterEach
    public void clear() {
        utils.tearDown();
    }


    @Test
    public void createTaskTest() throws Exception {
        User user = userRepository.findAll().get(0);
        TaskStatus taskStatus = taskStatusRepository.findAll().get(0);

        assertEquals(0, taskRepository.count());

        utils.addDefaultTask()
                .andExpect(status().isCreated());
        assertEquals(1, taskRepository.count());

        final Task task = taskRepository.findAll().get(0);
        assertEquals(task.getName(), TEST_TASK_NAME);
        assertEquals(task.getDescription(), TEST_DESCRIPTION);
        assertEquals(task.getTaskStatus().getName(), taskStatus.getName());
        assertEquals(task.getExecutor().getEmail(), user.getEmail());
        assertEquals(task.getAuthor().getEmail(), user.getEmail());
    }

    @Test
    public void getTaskByIdTest() throws Exception {
        utils.addDefaultTask().andExpect(status().isCreated());
        assertEquals(1, taskRepository.count());

        final Long id = taskRepository.findAll().get(0).getId();

        final var request = get(TASK_CONTROLLER_PATH + ID, id);
        final var response = utils.perform(request, TEST_EMAIL)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task task = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(task.getName(), TEST_TASK_NAME);
        assertEquals(task.getDescription(), TEST_DESCRIPTION);
    }

    @Test
    public void getAllTasks() throws Exception {
        utils.addDefaultTask();
        assertEquals(1, taskRepository.count());

        final var request = get(TASK_CONTROLLER_PATH);
        final var response = utils.perform(request, TEST_EMAIL)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(tasks.size(), 1);
    }

    @Test
    public void filterTaskByStatusTest() throws Exception {
        utils.addDefaultTask();
        assertEquals(1, taskRepository.count());
        final Long statusId = taskStatusRepository.findAll().get(0).getId();

        final var requestForOtherStatus = get(TASK_CONTROLLER_PATH + "?taskStatus=100");
        final var responseForOtherStatus = utils.perform(requestForOtherStatus, TEST_EMAIL)
                .andExpect(status().isOk()).andReturn().getResponse();
        final List<Task> filteredTasks1 = fromJson(
                responseForOtherStatus.getContentAsString(),
                new TypeReference<>() {
        });
        assertEquals(0, filteredTasks1.size());

        final var requestForNeededStatus = get(TASK_CONTROLLER_PATH + "?taskStatus=" + statusId);
        final var responseForNeededStatus = utils.perform(requestForNeededStatus, TEST_EMAIL)
                .andExpect(status().isOk()).andReturn().getResponse();
        final List<Task> filteredTasks2 = fromJson(
                responseForNeededStatus.getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals(1, filteredTasks2.size());
        assertEquals(filteredTasks2.get(0).getTaskStatus().getName(), TEST_STATUS);
    }

    @Test
    public void filterTaskByExecutorTest() throws Exception {
        utils.addDefaultTask();
        assertEquals(1, taskRepository.count());
        final Long executorId = userRepository.findAll().get(0).getId();

        final var requestForOtherExecutor = get(TASK_CONTROLLER_PATH + "?executorId=100");
        final var responseForOtherExecutor = utils.perform(requestForOtherExecutor, TEST_EMAIL)
                .andExpect(status().isOk()).andReturn().getResponse();
        final List<Task> filteredTasks1 = fromJson(
                responseForOtherExecutor.getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals(0, filteredTasks1.size());

        final var requestForNeededExecutor = get(TASK_CONTROLLER_PATH + "?executorId=" + executorId);
        final var responseForNeededExecutor = utils.perform(requestForNeededExecutor, TEST_EMAIL)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final List<Task> filteredTasks2 = fromJson(
                responseForNeededExecutor.getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals(1, filteredTasks2.size());
        assertEquals(filteredTasks2.get(0).getExecutor().getFirstName(), TEST_FIRST_NAME);
    }

    @Test
    public void filterTaskByLabelTest() throws Exception {
        utils.addDefaultTask();
        assertEquals(1, taskRepository.count());
        final Long labelId = labelRepository.findAll().get(0).getId();

        final var requestForOtherLabel = get(TASK_CONTROLLER_PATH + "?labels=100");
        final var responseForOtherLabel = utils.perform(requestForOtherLabel, TEST_EMAIL)
                .andExpect(status().isOk()).andReturn().getResponse();
        final List<Task> filteredTasks1 = fromJson(
                responseForOtherLabel.getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals(0, filteredTasks1.size());

        final var requestForNeededLabel = get(TASK_CONTROLLER_PATH + "?labels=" + labelId);
        final var responseForNeededLabel = utils.perform(requestForNeededLabel, TEST_EMAIL)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final List<Task> filteredTasks2 = fromJson(
                responseForNeededLabel.getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals(1, filteredTasks2.size());
        assertEquals(filteredTasks2.get(0).getLabels().
                stream()
                .findFirst()
                .get().getName(), TEST_LABEL_NAME);
    }

    @Test
    public void updateTaskTest() throws Exception {
        utils.addDefaultTask();
        assertEquals(1, taskRepository.count());

        final Task task = taskRepository.findAll().get(0);
        final Long userId = task.getExecutor().getId();
        final Long statusId = task.getTaskStatus().getId();
        final Long labelId = labelRepository.findAll().get(0).getId();

        final TaskDto expectTaskDto = new TaskDto(
                "newName",
                "newDescription",
                userId,
                statusId,
                new HashSet<>(Set.of(labelId))
        );

        final var request = put(TASK_CONTROLLER_PATH + ID, task.getId())
                .content(asJson(expectTaskDto))
                .contentType(APPLICATION_JSON);

        utils.perform(request, TEST_EMAIL)
                .andExpect(status().isOk());

        final Task actualTask = taskRepository.findAll().get(0);
        assertEquals(actualTask.getName(), expectTaskDto.getName());
        assertEquals(actualTask.getDescription(), expectTaskDto.getDescription());
    }

    @Test
    public void deleteTaskTest() throws Exception {
        utils.addDefaultTask();
        assertEquals(1, taskRepository.count());

        final Long id = taskRepository.findAll().get(0).getId();
        final var request = delete(TASK_CONTROLLER_PATH + ID, id);
        utils.perform(request, TEST_EMAIL)
                .andExpect(status().isOk());

        assertEquals(0, taskRepository.count());
    }
}
