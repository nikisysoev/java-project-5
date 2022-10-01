package hexlet.code.service.implementation;

import hexlet.code.dto.UserDto;
import hexlet.code.exceptions.DeleteException;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUser(final Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User createUser(final UserDto userDto) {
        final User user = fromDto(new User(), userDto);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(final Long id, final UserDto userDto) {
        final User userFromRepo = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        final User user = fromDto(userFromRepo, userDto);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(final Long id) throws DeleteException {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getAuthorTasks() != null || user.getExecutorTasks() != null) {
            throw new DeleteException("User have tasks, firstly delete tasks");
        }

        userRepository.delete(user);
    }

    @Override
    public String getCurrentUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public User getCurrentUser() {
        return userRepository.findByEmail(getCurrentUserName()).get();
    }

    private User fromDto(final User user, final UserDto userDto) {
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return user;
    }
}
