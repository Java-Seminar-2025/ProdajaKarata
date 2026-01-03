package com.football.ticketsale.controller;

import com.football.ticketsale.dto.UserDto;
import com.football.ticketsale.entity.UserEntity;
import com.football.ticketsale.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;  // CORRECT import
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/index")
    public String home() {
        return "index";
    }

    @GetMapping("/signup")
    public String showRegistrationForm(Model model) {
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "signup";  // matches your HTML filename
    }

    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto,
                               BindingResult result,
                               Model model) {

        UserEntity existingUserEmail = userService.findUserByEmail(userDto.getEmail());
        if (existingUserEmail != null) {
            result.rejectValue("email", null,
                    "There is already an account registered with this email");
        }

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