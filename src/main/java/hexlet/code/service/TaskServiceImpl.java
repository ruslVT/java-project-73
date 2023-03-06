package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final LabelRepository labelRepository;
    private final UserService userService;

    @Override
    public Task createNewTask(TaskDto taskDto) {
        Task newTask = buildTask(taskDto);
        return taskRepository.save(newTask);
    }

    @Override
    public Task updateTask(Long id, TaskDto taskDto) {
        final Task task = taskRepository.findById(id).get();
        merge(task, taskDto);
        return taskRepository.save(task);
    }

    private void merge(final Task task, final TaskDto dto) {
        final Task newTask = buildTask(dto);
        task.setName(newTask.getName());
        task.setDescription(newTask.getDescription());
        task.setTaskStatus(newTask.getTaskStatus());
        task.setAuthor(newTask.getAuthor());
        task.setExecutor(newTask.getExecutor());
        task.setLabels(newTask.getLabels());
    }

    private Task buildTask(final TaskDto taskDto) {
        final User currentUser = userService.getCurrentUser();

        return Task.builder()
                .name(taskDto.getName())
                .description(taskDto.getDescription())
                .author(currentUser)
                .executor(userRepository.findById(taskDto.getExecutorId()).get())
                .taskStatus(taskStatusRepository.findById(taskDto.getTaskStatusId()).get())
                .labels(taskDto.getLabelIds().stream()
                        .map(id -> labelRepository.findById(id).get())
                        .collect(Collectors.toSet()))
                .build();
    }

}
