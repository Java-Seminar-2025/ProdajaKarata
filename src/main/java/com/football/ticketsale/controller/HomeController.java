package com.football.ticketsale.controller;

import com.football.ticketsale.entity.UserEntity;
import com.football.ticketsale.repository.CountryRepository;
import com.football.ticketsale.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import com.football.ticketsale.service.MatchService;
import com.football.ticketsale.entity.MatchEntity;
import org.springframework.web.bind.annotation.RequestParam;


import java.time.LocalDateTime;
import java.util.List;

import com.football.ticketsale.dto.MatchFilterDto;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Controller
public class HomeController {

    private final UserService userService;
    private final MatchService matchService;
    private final CountryRepository countryRepository;


    public HomeController(UserService userService, MatchService matchService, CountryRepository countryRepository) {
        this.userService = userService;
        this.matchService = matchService;
        this.countryRepository = countryRepository;
    }

    @GetMapping("/home")
    public String showHomePage(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "competitions", required = false) String competitions,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(name = "dateFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(name = "dateTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(name = "timeFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime timeFrom,
            @RequestParam(name = "timeTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime timeTo,
            @RequestParam(name = "club", required = false) String club,
            @RequestParam(name = "city", required = false) String city
    ) {
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

        MatchFilterDto f = new MatchFilterDto();
        f.setQ(q);

        if (competitions != null && !competitions.isBlank()) {
            List<String> comps = Arrays.stream(competitions.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .toList();
            f.setCompetitions(comps);
        }

        f.setMinPrice(minPrice);
        f.setMaxPrice(maxPrice);
        f.setDateFrom(dateFrom);
        f.setDateTo(dateTo);
        f.setTimeFrom(timeFrom);
        f.setTimeTo(timeTo);
        f.setClub(club);
        f.setCity(city);

        model.addAttribute("filter", f);
        model.addAttribute("matches", matchService.getUpcomingMatches(f));
        model.addAttribute("competitionOptions", matchService.getCompetitionOptions());
        model.addAttribute("countries", countryRepository.findAll());
        return "home";
    }
}

