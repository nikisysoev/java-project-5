package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.LoginDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.security.JWTUtil;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static hexlet.code.controller.LabelController.LABEL_CONTROLLER;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static hexlet.code.security.SecurityConfig.LOGIN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestUtil {

    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final TaskStatusRepository taskStatusRepository;

    public TestUtil(final ObjectMapper objectMapper,
                    final MockMvc mockMvc,
                    final UserRepository userRepository,
                    final  JWTUtil jwtUtil,
                    final TaskStatusRepository taskStatusRepository) {
        this.objectMapper = objectMapper;
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.taskStatusRepository = taskStatusRepository;
    }

    public ResultActions regUser() throws Exception {
        UserDto userDto = new UserDto(
                "email@email.com",
                "firstName",
                "lastName",
                "password"
        );
        String content = objectMapper.writeValueAsString(userDto);

        return mockMvc.perform(post(USER_CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));
    }

    public ResultActions logUser() throws Exception {
        LoginDto loginDto = new  LoginDto(
                "email@email.com",
                "password"
        );
        String content = objectMapper.writeValueAsString(loginDto);

        return mockMvc.perform(post(LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));
    }

    public ResultActions createStatus(String error) throws Exception {
        regUser();
        logUser();

        final User user = userRepository.findAll().get(0);
        final String token = jwtUtil.generateToken(user.getEmail());

        final TaskStatusDto taskStatusDto = new TaskStatusDto("Новый");
        final String content = objectMapper.writeValueAsString(taskStatusDto);

        return mockMvc
                .perform(post(TASK_STATUS_CONTROLLER_PATH)
                                .header(AUTHORIZATION, token + error)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content));
    }

    public ResultActions createTask() throws Exception {
        createStatus("");

        final User user = userRepository.findAll().get(0);
        final String token = jwtUtil.generateToken(user.getEmail());
        final TaskStatus taskStatus = taskStatusRepository.findAll().get(0);

        final TaskDto taskDto = new TaskDto(
                "Name",
                "Description",
                user.getId(),
                taskStatus.getId(),
                null
        );
        final String content = objectMapper.writeValueAsString(taskDto);

        return mockMvc
                .perform(post(TASK_CONTROLLER_PATH)
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));
    }

    public ResultActions createLabel() throws Exception {
        regUser();
        logUser();

        final User user = userRepository.findAll().get(0);
        final String token = jwtUtil.generateToken(user.getEmail());

        final LabelDto labelDto = new LabelDto("Fix");
        final String content = objectMapper.writeValueAsString(labelDto);

        return mockMvc
                .perform(post(LABEL_CONTROLLER)
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));
    }
}
