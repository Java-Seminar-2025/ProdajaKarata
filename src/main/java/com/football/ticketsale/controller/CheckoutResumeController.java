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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
public class CheckoutResumeController {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final SeatReservationRepository seatReservationRepository;

    public CheckoutResumeController(UserRepository userRepository,
                                    TicketRepository ticketRepository,
                                    SeatReservationRepository seatReservationRepository) {
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.seatReservationRepository = seatReservationRepository;
    }

    @PostMapping("/checkout/resume")
    public String resume(@AuthenticationPrincipal UserDetails userDetails,
                         @RequestParam UUID matchId,
                         @RequestParam String sectionCode,
                         @RequestParam String reservedUntil,
                         RedirectAttributes ra) {

        UserEntity user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        LocalDateTime until = LocalDateTime.parse(reservedUntil);

        List<TicketEntity> reserved = ticketRepository.findReservedBatch(
                user.getUserUid(),
                matchId,
                sectionCode,
                until,
                CheckoutService.STATUS_RESERVED
        );

        if (reserved.isEmpty()) {
            ra.addFlashAttribute("error", "Reservation not found or expired.");
            return "redirect:/my-tickets";
        }

        SeatReservationEntity sr0 = seatReservationRepository.findByTicket(reserved.get(0))
                .orElseThrow(() -> new IllegalStateException("Reserved ticket has no seat reservation"));

        return "redirect:/checkout/" + sr0.getMatch().getMatchUid();
    }
}
