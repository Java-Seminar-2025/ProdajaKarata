package com.football.ticketsale.controller;

import com.football.ticketsale.dto.view.MyTicketsPageDto;
import com.football.ticketsale.service.MyTicketsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyTicketsController {

    private final MyTicketsService myTicketsService;

    public MyTicketsController(MyTicketsService myTicketsService) {
        this.myTicketsService = myTicketsService;
    }

    @GetMapping("/my-tickets")
    public String myTickets(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        MyTicketsPageDto page = myTicketsService.buildPage(userDetails.getUsername());

        model.addAttribute("username", page.getUsername());
        model.addAttribute("role", page.getRole());
        model.addAttribute("activeReservations", page.getActiveReservations());
        model.addAttribute("tickets", page.getTickets());

        return "my_tickets";
    }
}
