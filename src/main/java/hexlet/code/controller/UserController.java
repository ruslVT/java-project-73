package hexlet.code.controller;

import com.rollbar.notifier.Rollbar;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
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

import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@AllArgsConstructor
@RequestMapping("${base-url}" + USER_CONTROLLER_PATH)
public class UserController {
    public static final String USER_CONTROLLER_PATH = "/users";
    public static final String ID = "/{id}";
    private static final String ONLY_OWNER_BY_ID = """
            @userRepository.findById(#id).get().getEmail() == authentication.getName()
        """;

    private final UserRepository userRepository;
    private final UserService userService;

    private final Rollbar rollbar;

    @Operation(summary = "Create new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "422", description = "Invalid data")
    })
    @PostMapping
    @ResponseStatus(CREATED)
    public User registerNewUser(@RequestBody @Valid final UserDto userDto) {
        rollbar.debug("Here is some debug message");
        return userService.createNewUser(userDto);
    }

    @Operation(summary = "Get user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(ID)
    public User getUserById(@PathVariable final long id) {
        return userRepository.findById(id).get();
    }

    @Operation(summary = "Get list of all users")
    @ApiResponse(responseCode = "200", description = "List of users is loaded")
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .toList();
    }

    @Operation(summary = "Update user data by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User data updated"),
            @ApiResponse(responseCode = "422", description = "Invalid update data"),
            @ApiResponse(responseCode = "404", description = "User with that id not found"),
            @ApiResponse(responseCode = "403", description = "Incorrect owner trying updated data")
    })
    @SecurityRequirement(name = "jwtIn")
    @PutMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public User updateUser(@PathVariable final long id, @RequestBody @Valid final UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    @Operation(summary = "Delete user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User with that id not found"),
            @ApiResponse(responseCode = "403", description = "Incorrect owner trying deleted user"),
            @ApiResponse(responseCode = "422", description = "User is associated with the task and cannot be deleted")
    })
    @SecurityRequirement(name = "jwtIn")
    @DeleteMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void deleteUser(@PathVariable final long id) {
        userRepository.deleteById(id);
    }
}
