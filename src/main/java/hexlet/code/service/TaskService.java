package hexlet.code.service;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import java.util.List;

public interface TaskService {
    Task getTask(Long id);

    List<Task> getTasks(Predicate predicate);

    Task createTask(TaskDto taskDto);

    Task updateTask(Long id, TaskDto taskDto);

    void deleteTask(Long id);
}
