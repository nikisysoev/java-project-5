package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
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
import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
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
public class StatusControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final TestUtil testUtil;
    private final JWTUtil jwtUtil;
    private final TaskStatusRepository taskStatusRepository;

    @Autowired
    public StatusControllerTest(final MockMvc mockMvc,
                                final ObjectMapper objectMapper,
                                final UserRepository userRepository,
                                final TestUtil testUtil,
                                final JWTUtil jwtUtil,
                                final TaskStatusRepository taskStatusRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.testUtil = testUtil;
        this.jwtUtil = jwtUtil;
        this.taskStatusRepository = taskStatusRepository;
    }

    @Test
    void testCreateStatusPositive() throws Exception {
        final MockHttpServletResponse responsePost = testUtil.createStatus("")
                .andReturn()
                .getResponse();

        assertThat(responsePost.getStatus()).isEqualTo(201);
    }

    @Test
    void testCreateStatusNegative() throws Exception {
        final MockHttpServletResponse responsePost = testUtil.createStatus("broken")
                .andReturn()
                .getResponse();

        assertThat(responsePost.getStatus()).isEqualTo(401);
    }

    @Test
    void testGetStatusesPositive() throws Exception {
        testUtil.createStatus("");

        final User user = userRepository.findAll().get(0);
        final String token = jwtUtil.generateToken(user.getEmail());

        final MockHttpServletResponse responseGet = mockMvc
                .perform(
                        get(TASK_STATUS_CONTROLLER_PATH)
                                .header(AUTHORIZATION, token)
                )
                .andReturn()
                .getResponse();

        assertThat(responseGet.getStatus()).isEqualTo(200);
    }

    @Test
    void testGetStatusesNegative() throws Exception {
        testUtil.createStatus("");

        final User user = userRepository.findAll().get(0);
        final String token = jwtUtil.generateToken(user.getEmail());

        final MockHttpServletResponse responseGet = mockMvc
                .perform(
                        get(TASK_STATUS_CONTROLLER_PATH)
                                .header(AUTHORIZATION, token + "broken")
                )
                .andReturn()
                .getResponse();

        assertThat(responseGet.getStatus()).isEqualTo(401);
    }

    @Test
    void testGetStatusPositive() throws Exception {
        testUtil.createStatus("");

        final User user = userRepository.findAll().get(0);
        final String token = jwtUtil.generateToken(user.getEmail());

        final TaskStatus taskStatus = taskStatusRepository.findAll().get(0);

        final MockHttpServletResponse responseGet = mockMvc
                .perform(
                        get(TASK_STATUS_CONTROLLER_PATH + ID, taskStatus.getId())
                                .header(AUTHORIZATION, token)
                )
                .andReturn()
                .getResponse();

        assertThat(responseGet.getStatus()).isEqualTo(200);
    }

    @Test
    void testUpdateStatusPositive() throws Exception {
        final TaskStatusDto taskStatusDto = new TaskStatusDto("В работе");
        final String content = objectMapper.writeValueAsString(taskStatusDto);

        testUtil.createStatus("");

        final User user = userRepository.findAll().get(0);
        final String token = jwtUtil.generateToken(user.getEmail());

        final TaskStatus taskStatus = taskStatusRepository.findAll().get(0);

        final MockHttpServletResponse responsePut = mockMvc
                .perform(
                        put(TASK_STATUS_CONTROLLER_PATH + ID, taskStatus.getId())
                                .header(AUTHORIZATION, token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andReturn()
                .getResponse();

        assertThat(responsePut.getStatus()).isEqualTo(200);
    }

    @Test
    void testDeleteStatusPositive() throws Exception {
        testUtil.createStatus("");

        final User user = userRepository.findAll().get(0);
        final String token = jwtUtil.generateToken(user.getEmail());

        final TaskStatus taskStatus = taskStatusRepository.findAll().get(0);

        final MockHttpServletResponse responseDelete = mockMvc
                .perform(
                        delete(TASK_STATUS_CONTROLLER_PATH + ID, taskStatus.getId())
                                .header(AUTHORIZATION, token)
                )
                .andReturn()
                .getResponse();

        assertThat(responseDelete.getStatus()).isEqualTo(200);
    }
}
