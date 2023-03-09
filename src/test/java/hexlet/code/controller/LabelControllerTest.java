package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForTest;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
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
import static hexlet.code.controller.LabelController.ID;
import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.TEST_EMAIL;
import static hexlet.code.utils.TestUtils.TEST_LABEL_NAME;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForTest.class)
public class LabelControllerTest {

    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private TestUtils utils;

    @BeforeEach
    public void beforeEach() throws Exception {
        utils.regDefaultUser();
    }

    @AfterEach
    public void afterEach() {
        utils.tearDown();
    }

    @Test
    public void createLabelTest() throws Exception {
        assertEquals(0, labelRepository.count());

        utils.addDefaultLabel()
                .andExpect(status().isCreated());
        assertEquals(1, labelRepository.count());
        assertEquals(labelRepository.findAll().get(0).getName(), TEST_LABEL_NAME);
    }

    @Test
    public void createInvalidLabelTest() throws Exception {
        LabelDto invalidLabel = new LabelDto();
        final var request = post(LABEL_CONTROLLER_PATH)
                .content(asJson(invalidLabel))
                .contentType(APPLICATION_JSON);
        utils.perform(request, TEST_EMAIL)
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void getLabelByIdTest() throws Exception {
        utils.addDefaultLabel();
        assertEquals(1, labelRepository.count());

        final Label expectedLabel = labelRepository.findAll().get(0);
        final var request = get(LABEL_CONTROLLER_PATH + ID, expectedLabel.getId());
        final var response = utils.perform(request, TEST_EMAIL)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final Label actualLabel = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(actualLabel.getName(), expectedLabel.getName());
    }

    @Test
    public void getAllLabelsTest() throws Exception {
        utils.addDefaultLabel();
        assertEquals(1, labelRepository.count());

        final var request = get(LABEL_CONTROLLER_PATH);
        final var response = utils.perform(request, TEST_EMAIL)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final List<Label> labels = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(labels.size(), 1);
        assertEquals(labels.get(0).getName(), TEST_LABEL_NAME);
    }

    @Test
    public void updateLabelTest() throws Exception {
        utils.addDefaultLabel();
        assertEquals(1, labelRepository.count());

        final Long id = labelRepository.findAll().get(0).getId();
        LabelDto expectedLabelDto = new LabelDto("newName");
        final var request = put(LABEL_CONTROLLER_PATH + ID, id)
                .content(asJson(expectedLabelDto))
                .contentType(APPLICATION_JSON);
        utils.perform(request, TEST_EMAIL)
                .andExpect(status().isOk());

        final Label updatedLabel = labelRepository.findAll().get(0);
        assertEquals(updatedLabel.getName(), expectedLabelDto.getName());
        assertEquals(updatedLabel.getId(), id);
    }

    @Test
    public void deleteLabelTest() throws Exception {
        utils.addDefaultLabel();
        assertEquals(1, labelRepository.count());

        final Long id = labelRepository.findAll().get(0).getId();
        final var request = delete(LABEL_CONTROLLER_PATH + ID, id);
        utils.perform(request, TEST_EMAIL)
                        .andExpect(status().isOk());
        assertEquals(0, labelRepository.count());
    }

    @Test
    public void deleteWhenLabelAssociatedWithTaskTest() throws Exception {
        utils.addDefaultLabel();
        utils.addDefaultStatus();
        utils.addDefaultTask();

        final Long labelId = labelRepository.findAll().get(0).getId();
        final var request = delete(LABEL_CONTROLLER_PATH + ID, labelId);
        utils.perform(request, TEST_EMAIL)
                .andExpect(status().isUnprocessableEntity());
    }


}
