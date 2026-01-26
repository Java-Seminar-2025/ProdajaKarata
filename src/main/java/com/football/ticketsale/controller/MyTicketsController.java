package com.football.ticketsale.controller;

import com.football.ticketsale.entity.SeatReservationEntity;
import com.football.ticketsale.entity.TicketEntity;
import com.football.ticketsale.entity.UserEntity;
import com.football.ticketsale.repository.SeatReservationRepository;
import com.football.ticketsale.repository.TicketRepository;
import com.football.ticketsale.repository.UserRepository;
import com.football.ticketsale.service.CheckoutService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class MyTicketsController {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final SeatReservationRepository seatReservationRepository;

    public MyTicketsController(UserRepository userRepository,
                               TicketRepository ticketRepository,
                               SeatReservationRepository seatReservationRepository) {
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.seatReservationRepository = seatReservationRepository;
    }

    @GetMapping("/my-tickets")
    public String myTickets(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        UserEntity user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<TicketEntity> paid = ticketRepository.findByUserEntityAndStatus(user, CheckoutService.STATUS_PAID);

        Map<UUID, Integer> seatByTicket = new HashMap<>();
        for (TicketEntity t : paid) {
            seatReservationRepository.findByTicket(t)
                    .map(SeatReservationEntity::getSeatNumber)
                    .ifPresent(seat -> seatByTicket.put(t.getTicketUid(), seat));
        }

        model.addAttribute("tickets", paid);
        model.addAttribute("seatByTicket", seatByTicket);

        return "my_tickets";
    }
}
