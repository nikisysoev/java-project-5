package hexlet.code.service.implementation;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskService;
import hexlet.code.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    @Override
    public Task getTask(final Long id) {
        return taskRepository.findById(id).get();
    }

    @Override
    public List<Task> getTasks(final Predicate predicate) {
        return predicate == null ? taskRepository.findAll() : taskRepository.findAll(predicate);
    }

    @Override
    public Task createTask(final TaskDto taskDto) {
        final Task task = fromDto(new Task(), taskDto);
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(final Long id, final TaskDto taskDto) {
        final Task task = fromDto(taskRepository.findById(id).get(), taskDto);
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(final Long id) {
        taskRepository.deleteById(id);
    }

    private Task fromDto(final Task task, final TaskDto taskDto) {
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        //NotNull always
        task.setTaskStatus(new TaskStatus(taskDto.getTaskStatusId()));
        task.setAuthor(userService.getCurrentUser());

        //can be null
        if (taskDto.getExecutorId() != null) {
            task.setExecutor(new User(taskDto.getExecutorId()));
        }

        Set<Long> labelsIds = taskDto.getLabelIds();
        Set<Label> labels = null;

        if (!CollectionUtils.isEmpty(labelsIds)) {
            labels = taskDto.getLabelIds().stream()
                    .map(Label::new)
                    .collect(Collectors.toSet());
        }

        task.setLabels(labels);
        return task;
    }
}
