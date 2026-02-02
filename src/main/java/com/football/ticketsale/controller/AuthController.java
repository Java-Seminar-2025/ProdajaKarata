package com.football.ticketsale.controller;

import com.football.ticketsale.dto.UserRegistrationDto;
import com.football.ticketsale.entity.UserEntity;
import com.football.ticketsale.service.UserService;
import com.football.ticketsale.validation.RegistrationValidator;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/welcome")
    public String home() {
        return "welcome";
    }

    @GetMapping("/signup")
    public String showRegistrationForm(Model model) {
        UserRegistrationDto user = new UserRegistrationDto();
        model.addAttribute("user", user);
        return "signup";
    }

    @PostMapping("/signup/save")
    public String registration(@Valid @ModelAttribute("user") UserRegistrationDto userDto,
                               BindingResult result,
                               Model model) {
        registrationValidator.validate(userDto, result);

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
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