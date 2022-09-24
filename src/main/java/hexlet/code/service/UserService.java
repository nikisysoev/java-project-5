package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.exceptions.DeleteException;
import hexlet.code.model.User;

import java.util.List;

public interface UserService {

    User getUser(Long id);

    List<User> getUsers();

    User createUser(UserDto userDto);

    User updateUser(Long id, UserDto userDto);

    void deleteUser(Long id) throws DeleteException;

    String getCurrentUserName();

    User getCurrentUser();
}
