package com.football.ticketsale.controller;

import ch.qos.logback.core.util.StringUtil;
import com.football.ticketsale.entity.UserEntity;
import com.football.ticketsale.service.UserService;
import com.football.ticketsale.service.UserService.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class HomeController {
    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/home")
    public String showHomePage(Model model, @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();

        UserEntity fullUser = userService.findUserByUsername(username);

        model.addAttribute("username", username);
        model.addAttribute("fullName", fullUser.getFullName());
        model.addAttribute("email", fullUser.getEmail());
        model.addAttribute("role", StringUtils.capitalize(fullUser.getAuthorizationLevel()));

        return "home";
    }
}

// SecurityContex
// @AuthenticationPrincipal
// thymeleaf-extras-springsecurity6
// SecurityContexHolder