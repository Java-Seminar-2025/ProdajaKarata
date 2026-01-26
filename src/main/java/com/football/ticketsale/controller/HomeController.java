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
import com.football.ticketsale.service.MatchService;
import com.football.ticketsale.entity.MatchEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class HomeController {

    private final UserService userService;
    private final MatchService matchService;

    public HomeController(UserService userService, MatchService matchService) {
        this.userService = userService;
        this.matchService = matchService;
    }

    @GetMapping("/home")
    public String showHomePage(
            Model model, @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "q", required = false) String q) {
        String username = userDetails.getUsername();

        UserEntity fullUser = userService.findUserByUsername(username);

        model.addAttribute("username", username);
        model.addAttribute("fullName", fullUser.getFullName());
        model.addAttribute("email", fullUser.getEmail());
        model.addAttribute("role", StringUtils.capitalize(fullUser.getAuthorizationLevel()));

        List<MatchEntity> matches = matchService.getUpcomingMatches(q);
        model.addAttribute("matches", matches);
        model.addAttribute("q", q == null ? "" : q);

        return "home";
    }
}

