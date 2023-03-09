package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;

public interface TaskStatusService {

    TaskStatus createNewTaskStatus(TaskStatusDto dto);
    TaskStatus updateTaskStatus(long id, TaskStatusDto dto);

}
