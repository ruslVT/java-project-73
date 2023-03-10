package hexlet.code.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.component.JWTHelper;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.controller.TaskStatusController.STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestUtils {

    public static final String TEST_FIRST_NAME = "testFName";
    public static final String TEST_LAST_NAME = "testLName";
    public static final String TEST_EMAIL = "email@yandex.ru";
    public static final String TEST_WRONG_EMAIL = "wrong@yandex.ru";
    public static final String TEST_PASSWORD = "testPassword";
    public static final String TEST_STATUS = "defaultStatus";
    public static final String TEST_TASK_NAME = "taskName";
    public static final String TEST_DESCRIPTION = "taskDescription";
    public static final String TEST_LABEL_NAME = "testLabel";

    private final UserDto testDto = new UserDto(
            TEST_EMAIL,
            TEST_FIRST_NAME,
            TEST_LAST_NAME,
            TEST_PASSWORD
    );

    public UserDto getTestDto() {
        return testDto;
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private JWTHelper jwtHelper;

    public void tearDown() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        labelRepository.deleteAll();
        userRepository.deleteAll();
    }

    public User getUserByEmail(final String email) {
        return userRepository.findByEmail(email).get();
    }

    public ResultActions regDefaultUser() throws Exception {
        return regUser(testDto);
    }

    public ResultActions regUser(final UserDto userDto) throws Exception {
        final var request = post(USER_CONTROLLER_PATH)
                .content(asJson(userDto))
                .contentType(APPLICATION_JSON);

        return perform(request);
    }

    public ResultActions addDefaultStatus() throws Exception {
        TaskStatusDto defaultStatusDto = new TaskStatusDto(TEST_STATUS);
        final var request = post(STATUS_CONTROLLER_PATH)
                .content(asJson(defaultStatusDto))
                .contentType(APPLICATION_JSON);

        return perform(request, TEST_EMAIL);
    }

    public ResultActions addDefaultLabel() throws Exception {
        LabelDto defaultLabelDto = new LabelDto(TEST_LABEL_NAME);
        final var request = post(LABEL_CONTROLLER_PATH)
                .content(asJson(defaultLabelDto))
                .contentType(APPLICATION_JSON);

        return perform(request, TEST_EMAIL);
    }

    public ResultActions addDefaultTask() throws Exception {
        final Long userId = userRepository.findByEmail(TEST_EMAIL).get().getId();
        final Long statusId = taskStatusRepository.findByName(TEST_STATUS).get().getId();
        final Set<Long> labelIds = new HashSet<>(Set.of(labelRepository.findByName(TEST_LABEL_NAME).get().getId()));
        final TaskDto defaultTaskDto = new TaskDto(TEST_TASK_NAME, TEST_DESCRIPTION, userId, statusId, labelIds);
        final var request = post(TASK_CONTROLLER_PATH)
                .content(asJson(defaultTaskDto))
                .contentType(APPLICATION_JSON);

        return perform(request, TEST_EMAIL);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request, final String byUser) throws Exception {
        final String token = jwtHelper.expiring(Map.of("username", byUser));
        request.header(AUTHORIZATION, token);

        return perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    public static String asJson(final Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }

    public static <T> T fromJson(final String json, final TypeReference<T> to) throws JsonProcessingException {
        return MAPPER.readValue(json, to);
    }

}
