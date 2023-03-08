package hexlet.code.controller;


import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
public class WelcomeController {

    @GetMapping(path = "/welcome")
    public String getWelcome() {
        return "Welcome to Spring";
    }

}
