package com.football.ticketsale.controller;

import com.football.ticketsale.dto.MatchFilterDto;
import com.football.ticketsale.dto.home.HomePageDto;
import com.football.ticketsale.service.HomePageService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class HomeController {

    private final HomePageService homePageService;

    public HomeController(HomePageService homePageService) {
        this.homePageService = homePageService;
    }

    @GetMapping({"/", "/home"})
    public String homePage(
            @AuthenticationPrincipal UserDetails userDetails,
            Authentication authentication,
            @ModelAttribute("filter") MatchFilterDto filter,
            Model model
    ) {
        HomePageDto page = homePageService.loadHomePage(userDetails.getUsername(), authentication, filter);

        model.addAttribute("user", page.user());
        model.addAttribute("role", page.role());
        model.addAttribute("matches", page.matches());
        model.addAttribute("competitionOptions", page.competitionOptions());
        model.addAttribute("countries", page.countries());
        model.addAttribute("filter", page.filter());

        return "home";
    }
}
