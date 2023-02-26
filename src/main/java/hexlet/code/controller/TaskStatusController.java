package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
import lombok.AllArgsConstructor;
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

import static hexlet.code.controller.TaskStatusController.STATUS_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@AllArgsConstructor
@RequestMapping("${base-url}" + STATUS_CONTROLLER_PATH)
public class TaskStatusController {
    public static final String STATUS_CONTROLLER_PATH = "/statuses";
    public static final String ID = "/{id}";
    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusService taskStatusService;

    @PostMapping
    @ResponseStatus(CREATED)
    public TaskStatus createStatus(@RequestBody @Valid final TaskStatusDto statusDto) {
        return taskStatusService.createNewTaskStatus(statusDto);
    }

    @GetMapping(ID)
    public TaskStatus getStatusById(@PathVariable final long id) {
        return taskStatusRepository.findById(id).get();
    }

    @GetMapping
    public List<TaskStatus> getAllStatus() {
        return taskStatusRepository.findAll()
                .stream()
                .toList();
    }

    @PutMapping(ID)
    public TaskStatus updateStatus(@PathVariable final long id, @RequestBody @Valid final TaskStatusDto statusDto) {
        return taskStatusService.updateTaskStatus(id, statusDto);
    }

    @DeleteMapping(ID)
    public void deleteStatus(@PathVariable final long id) {
        taskStatusRepository.deleteById(id);
    }
}
