package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.exceptions.DeleteException;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
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
    private final TaskStatusRepository taskStatusRepository;

    @Operation(summary = "Get status")
    @GetMapping(UserController.ID)
    public TaskStatus getTaskStatus(@PathVariable final Long id) {
        return taskStatusRepository.findById(id).get();
    }

    @Operation(summary = "Get all statuses")
    @GetMapping
    public List<TaskStatus> getTaskStatuses() {
        return taskStatusRepository.findAll();
    }

    @Operation(summary = "Create new status")
    @ApiResponses(@ApiResponse(responseCode = "201", description = "Status created"))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatus createTaskStatus(@RequestBody @Valid final TaskStatusDto taskStatusDto) {
        final TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(taskStatusDto.getName());
        return taskStatusRepository.save(taskStatus);
    }

    @Operation(summary = "Update status")
    @PutMapping(UserController.ID)
    public TaskStatus updateTaskStatus(@PathVariable final Long id,
                                       @RequestBody @Valid final TaskStatusDto taskStatusDto) {

        final TaskStatus taskStatus = taskStatusRepository.findById(id).get();
        taskStatus.setName(taskStatusDto.getName());
        return taskStatusRepository.save(taskStatus);
    }

    @Operation(summary = "Delete status")
    @DeleteMapping(UserController.ID)
    public void deleteTaskStatus(@PathVariable final Long id) throws DeleteException {
        final TaskStatus taskStatus = taskStatusRepository.findById(id).get();

        if (!CollectionUtils.isEmpty(taskStatus.getTasks())) {
            throw new DeleteException("Status is used in tasks, firstly delete necessary tasks");
        }

        taskStatusRepository.delete(taskStatus);
    }
}
