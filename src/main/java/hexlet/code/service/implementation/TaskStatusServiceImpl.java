package hexlet.code.service.implementation;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.exceptions.DeleteException;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;

    @Override
    public TaskStatus getTaskStatus(final Long id) {
        return taskStatusRepository.findById(id).get();
    }

    @Override
    public List<TaskStatus> getTaskStatuses() {
        return taskStatusRepository.findAll();
    }

    @Override
    public TaskStatus createTaskStatus(final TaskStatusDto taskStatusDto) {
        final TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(taskStatusDto.getName());
        return taskStatusRepository.save(taskStatus);
    }

    @Override
    public TaskStatus updateTaskStatus(final Long id, final TaskStatusDto taskStatusDto) {
        final TaskStatus taskStatus = taskStatusRepository.findById(id).get();
        taskStatus.setName(taskStatusDto.getName());
        return taskStatusRepository.save(taskStatus);
    }

    @Override
    public void deleteTaskStatus(final Long id) throws DeleteException {
        final TaskStatus taskStatus = taskStatusRepository.findById(id).get();

        if (!taskStatus.getTasks().isEmpty()) {
            throw new DeleteException("Status is used in tasks, firstly delete necessary tasks");
        }

        taskStatusRepository.delete(taskStatus);
    }
}
