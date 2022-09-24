package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.exceptions.DeleteException;
import hexlet.code.model.TaskStatus;
import hexlet.code.service.TaskStatusService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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
import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;

@RestController
@RequestMapping("${base-url}" + TASK_STATUS_CONTROLLER_PATH)
@AllArgsConstructor
public class TaskStatusController {

    public static final String TASK_STATUS_CONTROLLER_PATH = "/statuses";
    private final TaskStatusService taskStatusService;

    @GetMapping(UserController.ID)
    public TaskStatus getTaskStatus(@PathVariable final Long id) {
        return taskStatusService.getTaskStatus(id);
    }

    @GetMapping
    public List<TaskStatus> getTaskStatuses() {
        return taskStatusService.getTaskStatuses();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatus createTaskStatus(@RequestBody @Valid final TaskStatusDto taskStatusDto) {
        return taskStatusService.createTaskStatus(taskStatusDto);
    }

    @PutMapping(UserController.ID)
    public TaskStatus updateTaskStatus(@PathVariable final Long id,
                                       @RequestBody @Valid final TaskStatusDto taskStatusDto) {

        return taskStatusService.updateTaskStatus(id, taskStatusDto);
    }

    @DeleteMapping(UserController.ID)
    public void deleteTaskStatus(@PathVariable final Long id) throws DeleteException {
        taskStatusService.deleteTaskStatus(id);
    }
}
