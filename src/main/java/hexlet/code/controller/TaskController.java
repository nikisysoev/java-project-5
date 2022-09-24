package hexlet.code.controller;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
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
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;

@RestController
@RequestMapping("${base-url}" + TASK_CONTROLLER_PATH)
@AllArgsConstructor
public class TaskController {

    public static final String TASK_CONTROLLER_PATH = "/tasks";
    private final TaskService taskService;
    private static final String TASK_OWNER =
            "@taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()";

    @GetMapping(UserController.ID)
    public Task getTask(@PathVariable final Long id) {
        return taskService.getTask(id);
    }

    @GetMapping
    public List<Task> getTasks(@QuerydslPredicate final Predicate predicate) {
        return taskService.getTasks(predicate);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@RequestBody @Valid final TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @PreAuthorize(TASK_OWNER)
    @PutMapping(UserController.ID)
    public Task updateTask(@PathVariable final Long id,
                           @RequestBody @Valid final TaskDto taskDto) {

        return taskService.updateTask(id, taskDto);
    }

    @PreAuthorize(TASK_OWNER)
    @DeleteMapping(UserController.ID)
    public void deleteTask(@PathVariable final Long id) {
        taskService.deleteTask(id);
    }
}
