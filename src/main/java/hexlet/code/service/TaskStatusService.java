package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.exceptions.DeleteException;
import hexlet.code.model.TaskStatus;

import java.util.List;

public interface TaskStatusService {
    TaskStatus getTaskStatus(Long id);

    List<TaskStatus> getTaskStatuses();

    TaskStatus createTaskStatus(TaskStatusDto taskStatusDto);

    TaskStatus updateTaskStatus(Long id, TaskStatusDto taskStatusDto);

    void deleteTaskStatus(Long id) throws DeleteException;
}
