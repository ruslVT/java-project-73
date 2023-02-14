package hexlet.code.controller;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;

@RestController
@RequestMapping("${base-url}" + USER_CONTROLLER_PATH)
public class UserController {
    public static final String USER_CONTROLLER_PATH = "/users";
    public static final String ID = "/{id}";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserServiceImpl userService;

    @PostMapping
    public void createNewUser(@RequestBody @Valid UserDto userDto) {
        userService.createNewUser(userDto);
    }

    @GetMapping(ID)
    public User getUserById(@PathVariable final long id) {
        return userRepository.findById(id).get();
    }

    @GetMapping
    public List<User> getAll() {
        return userRepository.findAll()
                .stream()
                .toList();
    }

    @PutMapping(ID)
    public void updateUserById(@PathVariable long id, @RequestBody @Valid UserDto userDto) {
        userService.updateUser(id, userDto);
    }

    @DeleteMapping(ID)
    public void deleteUser(@PathVariable long id) {
        User user = userRepository.findById(id).get();
        userRepository.delete(user);
    }
}
