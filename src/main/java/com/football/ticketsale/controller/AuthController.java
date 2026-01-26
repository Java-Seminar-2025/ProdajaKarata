package com.football.ticketsale.controller;

import com.football.ticketsale.dto.UserRegistrationDto;
import com.football.ticketsale.entity.UserEntity;
import com.football.ticketsale.service.UserService;
import com.football.ticketsale.validation.RegistrationValidator;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;
    private final RegistrationValidator registrationValidator;

    public AuthController(UserService userService, RegistrationValidator registrationValidator) {
        this.userService = userService;
        this.registrationValidator = registrationValidator;
    }

    @InitBinder("user")
    void initBinder(WebDataBinder binder) {
        binder.addValidators(registrationValidator);
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }

    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/home";
    }

    @GetMapping("/signup")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "signup";
    }

    @PostMapping("/signup/save")
    public String registration(@Valid @ModelAttribute("user") UserRegistrationDto userDto,
                               BindingResult result) {
        if (result.hasErrors()) {
            return "signup";
        }

        userService.saveUser(userDto);
        return "redirect:/login?success";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}