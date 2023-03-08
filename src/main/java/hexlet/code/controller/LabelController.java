package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
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

import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@AllArgsConstructor
@SecurityRequirement(name = "jwtIn")
@RequestMapping("${base-url}" + LABEL_CONTROLLER_PATH)
public class LabelController {

    public static final String LABEL_CONTROLLER_PATH = "/labels";
    public static final String ID = "/{id}";

    private final LabelRepository labelRepository;
    private final LabelService labelService;

    @Operation(summary = "Create new label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Label created"),
            @ApiResponse(responseCode = "422", description = "Invalid data")
    })
    @PostMapping
    @ResponseStatus(CREATED)
    public Label createLabel(@RequestBody @Valid LabelDto labelDto) {
        return labelService.createLabel(labelDto);
    }

    @Operation(summary = "Get label by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label found"),
            @ApiResponse(responseCode = "404", description = "Label not found")
    })
    @GetMapping(ID)
    public Label getLabelById(@PathVariable Long id) {
        return labelRepository.findById(id).get();
    }

    @Operation(summary = "Get list of all labels")
    @ApiResponse(responseCode = "200", description = "List of labels is loaded")
    @GetMapping
    public List<Label> getAllLabels() {
        return labelRepository.findAll()
                .stream()
                .toList();
    }

    @Operation(summary = "Update label data by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label data updated"),
            @ApiResponse(responseCode = "422", description = "Invalid update data"),
            @ApiResponse(responseCode = "404", description = "Label with that id not found"),
    })
    @PutMapping(ID)
    public Label updateLabel(@PathVariable Long id, @RequestBody @Valid LabelDto labelDto) {
        return labelService.updateLabel(id, labelDto);
    }

    @Operation(summary = "Delete label by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label deleted"),
            @ApiResponse(responseCode = "404", description = "Label with that id not found"),
            @ApiResponse(responseCode = "422", description = "Label is associated with the task and cannot be deleted")
    })
    @DeleteMapping(ID)
    public void deleteLabel(@PathVariable Long id) {
        labelRepository.deleteById(id);
    }
}
