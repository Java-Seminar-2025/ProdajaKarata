package com.football.ticketsale.controller;

import com.football.ticketsale.entity.SeatReservationEntity;
import com.football.ticketsale.entity.TicketEntity;
import com.football.ticketsale.entity.UserEntity;
import com.football.ticketsale.repository.SeatReservationRepository;
import com.football.ticketsale.repository.StadiumSectionRepository;
import com.football.ticketsale.repository.TicketRepository;
import com.football.ticketsale.repository.UserRepository;
import com.football.ticketsale.service.CheckoutService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.*;

@Controller
public class MyTicketsController {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final SeatReservationRepository seatReservationRepository;
    private final StadiumSectionRepository stadiumSectionRepository;

    public MyTicketsController(UserRepository userRepository,
                               TicketRepository ticketRepository,
                               SeatReservationRepository seatReservationRepository,
                               StadiumSectionRepository stadiumSectionRepository) {
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.seatReservationRepository = seatReservationRepository;
        this.stadiumSectionRepository = stadiumSectionRepository;
    }

    public record ReservationGroup(
            UUID matchId,
            String sectionCode,
            LocalDateTime reservedUntil,
            String ownerName,
            int quantity,
            List<UUID> ticketIds
    ) {}

    @GetMapping("/my-tickets")
    public String myTickets(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        UserEntity user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));


        model.addAttribute("user", user);

        List<TicketEntity> paid = ticketRepository.findByUserEntityAndStatus(user, CheckoutService.STATUS_PAID);

        Map<UUID, Integer> seatByTicket = new HashMap<>();
        Map<UUID, String> sectionByTicket = new HashMap<>();
        Map<UUID, Boolean> refundable = new HashMap<>();

        for (TicketEntity t : paid) {
            SeatReservationEntity sr = seatReservationRepository.findByTicket(t).orElse(null);

            // Seat info
            if (sr != null) {
                seatByTicket.put(t.getTicketUid(), sr.getSeatNumber());
            }

            // Section info (older tickets may have NULL section_code in DB; backfill it)
            String sectionCode = t.getSectionCode();
            if ((sectionCode == null || sectionCode.isBlank()) && sr != null) {
                sectionCode = resolveSectionCode(sr);
                if (sectionCode != null && !sectionCode.isBlank()) {
                    t.setSectionCode(sectionCode);
                    ticketRepository.save(t);
                }
            }
            sectionByTicket.put(t.getTicketUid(), sectionCode);

            // Refund rule
            if (sr != null) {
                boolean allowed = sr.getMatch().getMatchDatetime().isAfter(LocalDateTime.now().plusDays(3));
                refundable.put(t.getTicketUid(), allowed);
            }
        }


        List<TicketEntity> activeReserved = ticketRepository
                .findByUserEntityAndStatusAndReservedUntilAfter(user, CheckoutService.STATUS_RESERVED, LocalDateTime.now());

        Map<String, ReservationGroup> grouped = new LinkedHashMap<>();
        for (TicketEntity t : activeReserved) {
            SeatReservationEntity sr = seatReservationRepository.findByTicket(t).orElse(null);
            if (sr == null) continue;

            UUID matchId = sr.getMatch().getMatchUid();
            String section = t.getSectionCode() != null ? t.getSectionCode() : "—";
            LocalDateTime until = t.getReservedUntil();
            String owner = t.getOwnerName();

            String key = matchId + "|" + section + "|" + until + "|" + owner;

            ReservationGroup g = grouped.get(key);
            if (g == null) {
                grouped.put(key, new ReservationGroup(
                        matchId, section, until, owner, 1,
                        new ArrayList<>(List.of(t.getTicketUid()))
                ));
            } else {
                g.ticketIds().add(t.getTicketUid());
                grouped.put(key, new ReservationGroup(
                        g.matchId(), g.sectionCode(), g.reservedUntil(), g.ownerName(),
                        g.quantity() + 1, g.ticketIds()
                ));
            }
        }

        model.addAttribute("tickets", paid);
        model.addAttribute("seatByTicket", seatByTicket);
        model.addAttribute("sectionByTicket", sectionByTicket);
        model.addAttribute("refundable", refundable);

        model.addAttribute("activeReservations", grouped.values());

        return "my_tickets";
    }

    /**
     * Backfills section code for older rows where TicketEntity.sectionCode is null.
     */
    private String resolveSectionCode(SeatReservationEntity sr) {
        if (sr == null || sr.getMatch() == null || sr.getMatch().getStadium() == null) {
            return null;
        }
        final int seat = sr.getSeatNumber();
        return stadiumSectionRepository.findByStadium(sr.getMatch().getStadium())
                .stream()
                .filter(sec -> sec.getSeatStart() != null && sec.getSeatEnd() != null)
                .filter(sec -> seat >= sec.getSeatStart() && seat <= sec.getSeatEnd())
                .map(sec -> sec.getSectionCode())
                .findFirst()
                .orElse(null);
    }
}
