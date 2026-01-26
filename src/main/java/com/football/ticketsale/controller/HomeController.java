package com.football.ticketsale.controller;

import com.football.ticketsale.entity.UserEntity;
import com.football.ticketsale.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/home")
    public String showHomePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        String username = userDetails.getUsername();
        UserEntity fullUser = userService.findUserByUsername(username);

        if (fullUser == null) {
            return "redirect:/logout";
        }

        model.addAttribute("user", fullUser);
        model.addAttribute("role", StringUtils.capitalize(fullUser.getAuthorizationLevel()));

        return "home";
    }
}
