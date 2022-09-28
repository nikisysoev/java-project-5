package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.security.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static hexlet.code.controller.LabelController.LABEL_CONTROLLER;
import static hexlet.code.controller.UserController.ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Transactional
@ActiveProfiles("test")
public class LabelsControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final TestUtil testUtil;
    private final JWTUtil jwtUtil;
    private final LabelRepository labelRepository;

    @Autowired
    public LabelsControllerTest(final MockMvc mockMvc,
                                final ObjectMapper objectMapper,
                                final UserRepository userRepository,
                                final TestUtil testUtil,
                                final JWTUtil jwtUtil,
                                final LabelRepository labelRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.testUtil = testUtil;
        this.jwtUtil = jwtUtil;
        this.labelRepository = labelRepository;
    }

    @Test
    void testCreateLabelPositive() throws Exception {
        final MockHttpServletResponse responsePost = testUtil.createLabel()
                .andReturn()
                .getResponse();

        assertThat(responsePost.getStatus()).isEqualTo(201);
    }

    @Test
    void testGetLabelsPositive() throws Exception {
        testUtil.createLabel();

        final User user = userRepository.findAll().get(0);
        final String token = jwtUtil.generateToken(user.getEmail());

        final MockHttpServletResponse responseGet = mockMvc
                .perform(
                        get(LABEL_CONTROLLER)
                                .header(AUTHORIZATION, token)
                )
                .andReturn()
                .getResponse();

        assertThat(responseGet.getStatus()).isEqualTo(200);
    }

    @Test
    void testGetLabelPositive() throws Exception {
        testUtil.createLabel();

        final User user = userRepository.findAll().get(0);
        final String token = jwtUtil.generateToken(user.getEmail());

        final Label label = labelRepository.findAll().get(0);

        final MockHttpServletResponse responseGet = mockMvc
                .perform(
                        get(LABEL_CONTROLLER + ID, label.getId())
                                .header(AUTHORIZATION, token)
                )
                .andReturn()
                .getResponse();

        assertThat(responseGet.getStatus()).isEqualTo(200);
    }

    @Test
    void testUpdateStatusPositive() throws Exception {
        final LabelDto labelDto = new LabelDto("Bug");
        final String content = objectMapper.writeValueAsString(labelDto);

        testUtil.createLabel();

        final User user = userRepository.findAll().get(0);
        final String token = jwtUtil.generateToken(user.getEmail());

        final Label label = labelRepository.findAll().get(0);

        final MockHttpServletResponse responsePut = mockMvc
                .perform(
                        put(LABEL_CONTROLLER + ID, label.getId())
                                .header(AUTHORIZATION, token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andReturn()
                .getResponse();

        assertThat(responsePut.getStatus()).isEqualTo(200);
        assertThat(responsePut.getContentAsString()).contains("Bug");
    }

    @Test
    void testDeleteLabelPositive() throws Exception {
        testUtil.createLabel();

        final User user = userRepository.findAll().get(0);
        final String token = jwtUtil.generateToken(user.getEmail());

        final Label label = labelRepository.findAll().get(0);

        final MockHttpServletResponse responseDelete = mockMvc
                .perform(
                        delete(LABEL_CONTROLLER + ID, label.getId())
                                .header(AUTHORIZATION, token)
                )
                .andReturn()
                .getResponse();

        assertThat(responseDelete.getStatus()).isEqualTo(200);
    }

}
