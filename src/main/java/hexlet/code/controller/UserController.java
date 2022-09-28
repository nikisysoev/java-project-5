package hexlet.code.controller;

import hexlet.code.dto.UserDto;
import hexlet.code.exceptions.DeleteException;
import hexlet.code.model.User;
import hexlet.code.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.List;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;

@RestController
@RequestMapping("${base-url}" + USER_CONTROLLER_PATH)
@AllArgsConstructor
public class UserController {

    public static final String USER_CONTROLLER_PATH = "/users";
    public static final String ID = "/{id}";
    private final UserService userService;
    private static final String ONLY_OWNER_BY_ID =
            "@userRepository.findById(#id).get().getEmail() == authentication.getName()";

    @Operation(summary = "Get user")
    @GetMapping(ID)
    public User getUser(@PathVariable final Long id) {
        return userService.getUser(id);
    }

    @Operation(summary = "Get all users")
    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @Operation(summary = "Create new user")
    @ApiResponses(@ApiResponse(responseCode = "201", description = "User created"))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User registerUser(@RequestBody @Valid final UserDto userDto) {
        return userService.createUser(userDto);
    }


    @Operation(summary = "Update user")
    @PutMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public User updateUser(@PathVariable final Long id, @RequestBody @Valid final UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    @Operation(summary = "Delete user")
    @DeleteMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void deleteUser(@PathVariable final Long id) throws DeleteException {
        userService.deleteUser(id);
    }
}
