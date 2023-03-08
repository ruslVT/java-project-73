package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = "jwtIn")
@RequestMapping("${base-url}" + STATUS_CONTROLLER_PATH)
public class TaskStatusController {
    public static final String STATUS_CONTROLLER_PATH = "/statuses";
    public static final String ID = "/{id}";
    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusService taskStatusService;

    @Operation(summary = "create new status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Status created"),
            @ApiResponse(responseCode = "422", description = "Invalid data")
    })
    @PostMapping
    @ResponseStatus(CREATED)
    public TaskStatus createStatus(@RequestBody @Valid final TaskStatusDto statusDto) {
        return taskStatusService.createNewTaskStatus(statusDto);
    }

    @Operation(summary = "Get status by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status found"),
            @ApiResponse(responseCode = "404", description = "Status not found")
    })
    @GetMapping(ID)
    public TaskStatus getStatusById(@PathVariable final long id) {
        return taskStatusRepository.findById(id).get();
    }

    @Operation(summary = "Get list of all status")
    @ApiResponse(responseCode = "200", description = "List of status is loaded")
    @GetMapping
    public List<TaskStatus> getAllStatus() {
        return taskStatusRepository.findAll()
                .stream()
                .toList();
    }

    @Operation(summary = "Update status data by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status data updated"),
            @ApiResponse(responseCode = "422", description = "Invalid update data"),
            @ApiResponse(responseCode = "404", description = "Status with that id not found"),
    })
    @PutMapping(ID)
    public TaskStatus updateStatus(@PathVariable final long id, @RequestBody @Valid final TaskStatusDto statusDto) {
        return taskStatusService.updateTaskStatus(id, statusDto);
    }

    @Operation(summary = "Delete status by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status deleted"),
            @ApiResponse(responseCode = "404", description = "Status with that id not found"),
            @ApiResponse(responseCode = "422", description = "Status is associated with the task and cannot be deleted")
    })
    @DeleteMapping(ID)
    public void deleteStatus(@PathVariable final long id) {
        taskStatusRepository.deleteById(id);
    }
}
