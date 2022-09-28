package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
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
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
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
public class TasksControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final TestUtil testUtil;
    private final JWTUtil jwtUtil;
    private final TaskStatusRepository taskStatusRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public TasksControllerTest(final MockMvc mockMvc,
                               final ObjectMapper objectMapper,
                               final UserRepository userRepository,
                               final TestUtil testUtil,
                               final JWTUtil jwtUtil,
                               final TaskStatusRepository taskStatusRepository,
                               final TaskRepository taskRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.testUtil = testUtil;
        this.jwtUtil = jwtUtil;
        this.taskStatusRepository = taskStatusRepository;
        this.taskRepository = taskRepository;
    }

    @Test
    void testCreateTaskPositive() throws Exception {
        final MockHttpServletResponse responsePost = testUtil.createTask()
                .andReturn()
                .getResponse();

        assertThat(responsePost.getStatus()).isEqualTo(201);
    }

    @Test
    void testGetTasksPositive() throws Exception {
        testUtil.createTask();

        final User user = userRepository.findAll().get(0);
        final String token = jwtUtil.generateToken(user.getEmail());

        final MockHttpServletResponse responseGet = mockMvc
                .perform(
                        get(TASK_CONTROLLER_PATH)
                                .header(AUTHORIZATION, token)
                )
                .andReturn()
                .getResponse();

        assertThat(responseGet.getStatus()).isEqualTo(200);
    }

    @Test
    void testGetTaskPositive() throws Exception {
        testUtil.createTask();

        final User user = userRepository.findAll().get(0);
        final String token = jwtUtil.generateToken(user.getEmail());

        final Task task = taskRepository.findAll().get(0);

        final MockHttpServletResponse responseGet = mockMvc
                .perform(
                        get(TASK_CONTROLLER_PATH + ID, task.getId())
                                .header(AUTHORIZATION, token)
                )
                .andReturn()
                .getResponse();

        assertThat(responseGet.getStatus()).isEqualTo(200);
    }

    @Test
    void testGetTaskWithParametersPositive() throws Exception {
        testUtil.createTask();

        final User user = userRepository.findAll().get(0);
        final String token = jwtUtil.generateToken(user.getEmail());
        final TaskStatus taskStatus = taskStatusRepository.findAll().get(0);
        final String parameters = "?taskStatus=" + taskStatus.getId() + "&executorId=" + user.getId();

        final MockHttpServletResponse responseGet = mockMvc
                .perform(
                        get(TASK_CONTROLLER_PATH + parameters)
                                .header(AUTHORIZATION, token)
                )
                .andReturn()
                .getResponse();

        assertThat(responseGet.getStatus()).isEqualTo(200);
        assertThat(responseGet.getContentAsString()).contains("Name");
    }

    @Test
    void testUpdateTaskPositive() throws Exception {
        testUtil.createTask();

        final User user = userRepository.findAll().get(0);
        final String token = jwtUtil.generateToken(user.getEmail());
        final Task task = taskRepository.findAll().get(0);
        final TaskStatus taskStatus = taskStatusRepository.findAll().get(0);

        final TaskDto taskDto = new TaskDto(
                "Newname",
                "Newdescription",
                user.getId(),
                taskStatus.getId(),
                null
        );
        final String content = objectMapper.writeValueAsString(taskDto);

        final MockHttpServletResponse responsePut = mockMvc
                .perform(put(TASK_CONTROLLER_PATH + ID, task.getId())
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andReturn()
                .getResponse();

        assertThat(responsePut.getStatus()).isEqualTo(200);
    }

    @Test
    void testDeleteTaskPositive() throws Exception {
        testUtil.createTask();

        final User user = userRepository.findAll().get(0);
        final String token = jwtUtil.generateToken(user.getEmail());
        final Task task = taskRepository.findAll().get(0);

        final MockHttpServletResponse responsePut = mockMvc
                .perform(delete(TASK_CONTROLLER_PATH + ID, task.getId())
                        .header(AUTHORIZATION, token)
                )
                .andReturn()
                .getResponse();

        assertThat(responsePut.getStatus()).isEqualTo(200);
    }
}

