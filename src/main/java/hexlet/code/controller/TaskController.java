package hexlet.code.controller;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
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
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@AllArgsConstructor
@SecurityRequirement(name = "jwtIn")
@RequestMapping("${base-url}" + TASK_CONTROLLER_PATH)
public class TaskController {

    public static final String TASK_CONTROLLER_PATH = "/tasks";
    public static final String ID = "/{id}";
    private static final String ONLY_AUTHOR_BY_ID = """
            @taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()
        """;

    private final TaskRepository taskRepository;
    private final TaskService taskService;

    @Operation(summary = "Create new task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created"),
            @ApiResponse(responseCode = "422", description = "Invalid data")
    })
    @PostMapping
    @ResponseStatus(CREATED)
    public Task createTask(@RequestBody @Valid final TaskDto taskDto) {
        return taskService.createNewTask(taskDto);
    }

    @Operation(summary = "Get task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task found"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping(ID)
    public Task getTaskById(@PathVariable final Long id) {
        return taskRepository.findById(id).get();
    }

    @Operation(summary = "Get list of all tasks")
    @ApiResponse(responseCode = "200", description = "List of users is loaded")
    @GetMapping
    public List<Task> getAllTasks(@QuerydslPredicate(root = Task.class) Predicate predicate) {
        return (List<Task>) taskRepository.findAll(predicate);
    }

    @Operation(summary = "Update task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task data updated"),
            @ApiResponse(responseCode = "422", description = "Invalid update data"),
            @ApiResponse(responseCode = "404", description = "Task with that id not found"),
            @ApiResponse(responseCode = "403", description = "Incorrect owner trying updated data")
    })
    @PutMapping(ID)
    @PreAuthorize(ONLY_AUTHOR_BY_ID)
    public Task updateTask(@PathVariable final Long id, @RequestBody @Valid final TaskDto taskDto) {
        return taskService.updateTask(id, taskDto);
    }

    @Operation(summary = "Delete task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task deleted"),
            @ApiResponse(responseCode = "404", description = "Task with that id not found"),
            @ApiResponse(responseCode = "403", description = "Incorrect owner trying deleted user"),
    })
    @DeleteMapping(ID)
    @PreAuthorize(ONLY_AUTHOR_BY_ID)
    public void deleteTask(@PathVariable final Long id) {
        taskRepository.deleteById(id);
    }
}
