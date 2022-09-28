package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.LoginDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
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
import static hexlet.code.controller.UserController.ID;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static hexlet.code.security.SecurityConfig.LOGIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Transactional
@ActiveProfiles("test")
public class UserControllerTest {

    private final UserDto userDto = new UserDto(
            "Bob@gmail.com",
            "Bob",
            "Bob",
            "Bob"
    );
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final TestUtil testUtil;
    private final JWTUtil jwtUtil;

    @Autowired
    public UserControllerTest(final MockMvc mockMvc,
                              final ObjectMapper objectMapper,
                              final UserRepository userRepository,
                              final TestUtil testUtil,
                              final JWTUtil jwtUtil) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.testUtil = testUtil;
        this.jwtUtil = jwtUtil;
    }

    @Test
    void testRegUserPositive() throws Exception {
        assertThat(userRepository.count()).isEqualTo(0);

        final MockHttpServletResponse responsePost = testUtil.regUser()
                .andReturn()
                .getResponse();

        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(responsePost.getStatus()).isEqualTo(201);

        final User actualUser = userRepository.findByEmail("email@email.com").get();
        assertThat(actualUser).isNotNull();
        assertThat(actualUser.getFirstName()).isEqualTo("firstName");
        assertThat(actualUser.getCreatedAt()).isNotNull();
        assertThat(actualUser.getPassword()).isNotEqualTo("password");
    }

    @Test
    void testRegUserNegative() throws Exception {
        final MockHttpServletResponse responsePost1 = testUtil.regUser()
                .andReturn()
                .getResponse();
        assertThat(responsePost1.getStatus()).isEqualTo(201);

        final MockHttpServletResponse responsePost2 = testUtil.regUser()
                .andReturn()
                .getResponse();
        assertThat(responsePost2.getStatus()).isEqualTo(422);
    }

    @Test
    void testLogUserPositive() throws Exception {
        testUtil.regUser();

        final MockHttpServletResponse responsePost = testUtil.logUser()
                .andReturn()
                .getResponse();

        assertThat(responsePost.getStatus()).isEqualTo(200);
    }

    @Test
    void testLogUserNegative() throws Exception {
        final MockHttpServletResponse responsePost1 = testUtil.logUser()
                .andReturn()
                .getResponse();

        assertThat(responsePost1.getStatus()).isEqualTo(401);

        final LoginDto loginDto = new  LoginDto(
                "email@email.com",
                ""
        );
        final String content = objectMapper.writeValueAsString(loginDto);
        final MockHttpServletResponse responsePost2 = mockMvc.perform(post(LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andReturn()
                .getResponse();

        assertThat(responsePost2.getStatus()).isEqualTo(401);
    }

    @Test
    void testGetUsers() throws Exception {
         testUtil.regUser();

        final MockHttpServletResponse responseGet = mockMvc
                .perform(get(USER_CONTROLLER_PATH))
                .andReturn()
                .getResponse();

        assertThat(responseGet.getStatus()).isEqualTo(200);
        assertThat(responseGet.getContentAsString()).contains("firstName");
        assertThat(responseGet.getContentAsString()).doesNotContain("password");
    }

    @Test
    void testGetUserPositive() throws Exception {
        testUtil.regUser();
        testUtil.logUser();

        final User user = userRepository.findAll().get(0);
        final String token = jwtUtil.generateToken(user.getEmail());

        final MockHttpServletResponse responseGet = mockMvc
                .perform(
                        get(USER_CONTROLLER_PATH + ID, user.getId())
                                .header(AUTHORIZATION, token)
                )
                .andReturn()
                .getResponse();

        assertThat(responseGet.getStatus()).isEqualTo(200);
    }

    @Test
    void testGetUserNegative() throws Exception {
        testUtil.regUser();
        final User user = userRepository.findAll().get(0);

        final MockHttpServletResponse responseGet = mockMvc
                .perform(get(USER_CONTROLLER_PATH + ID, user.getId()))
                .andReturn()
                .getResponse();

        assertThat(responseGet.getStatus()).isEqualTo(401);
    }

    @Test
    void testUpdateUserPositive() throws Exception {
        final String content = objectMapper.writeValueAsString(userDto);

        testUtil.regUser();
        testUtil.logUser();

        final User user = userRepository.findAll().get(0);
        final String token = jwtUtil.generateToken(user.getEmail());

        final MockHttpServletResponse responsePut = mockMvc
                .perform(
                        put(USER_CONTROLLER_PATH + ID, user.getId())
                                .header(AUTHORIZATION, token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andReturn()
                .getResponse();

        assertThat(responsePut.getStatus()).isEqualTo(200);
    }

    @Test
    void testUpdateUserNegative() throws Exception {
        final String content = objectMapper.writeValueAsString(userDto);

        testUtil.regUser();
        testUtil.logUser();

        final User user = userRepository.findAll().get(0);
        final String token = jwtUtil.generateToken(user.getEmail());

        final MockHttpServletResponse responsePut = mockMvc
                .perform(
                        put(USER_CONTROLLER_PATH + ID, user.getId() + 1)
                                .header(AUTHORIZATION, token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andReturn()
                .getResponse();

        assertThat(responsePut.getStatus()).isEqualTo(401);
    }

    @Test
    void testDeleteUserPositive() throws Exception {
        testUtil.regUser();
        testUtil.logUser();

        final User user = userRepository.findAll().get(0);
        final String token = jwtUtil.generateToken(user.getEmail());

        final MockHttpServletResponse responseDelete = mockMvc
                .perform(
                        delete(USER_CONTROLLER_PATH + ID, user.getId())
                                .header(AUTHORIZATION, token)
                )
                .andReturn()
                .getResponse();

        assertThat(responseDelete.getStatus()).isEqualTo(200);
    }

    @Test
    void testDeleteUserNegative() throws Exception {
        testUtil.regUser();
        testUtil.logUser();

        final User user = userRepository.findAll().get(0);
        final String token = jwtUtil.generateToken(user.getEmail());

        final MockHttpServletResponse responseDelete = mockMvc
                .perform(
                        delete(USER_CONTROLLER_PATH + ID, user.getId() + 1)
                                .header(AUTHORIZATION, token)
                )
                .andReturn()
                .getResponse();

        assertThat(responseDelete.getStatus()).isEqualTo(401);
    }
}
