package com.football.ticketsale.controller;

import com.football.ticketsale.dto.checkout.PayRequestDto;
import com.football.ticketsale.dto.checkout.ReserveSectionRequestDto;
import com.football.ticketsale.dto.checkout.ReserveSectionResponseDto;
import com.football.ticketsale.entity.MatchEntity;
import com.football.ticketsale.entity.StadiumSectionEntity;
import com.football.ticketsale.entity.TicketTierEntity;
import com.football.ticketsale.entity.UserEntity;
import com.football.ticketsale.repository.MatchRepository;
import com.football.ticketsale.repository.TicketTierRepository;
import com.football.ticketsale.repository.UserRepository;
import com.football.ticketsale.service.CheckoutService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final TicketTierRepository ticketTierRepository;


    public CheckoutController(
            CheckoutService checkoutService,
            MatchRepository matchRepository,
            UserRepository userRepository,
            TicketTierRepository ticketTierRepository
    ) {
        this.checkoutService = checkoutService;
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
        this.ticketTierRepository = ticketTierRepository;
    }

    @GetMapping("/checkout/{matchId}")
    public String checkoutPage(@PathVariable UUID matchId,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {

        UserEntity user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        MatchEntity match = matchRepository.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("Match not found"));

        model.addAttribute("user", user);
        model.addAttribute("match", match);
        model.addAttribute("tiers", ticketTierRepository.findAll());

        ReserveSectionResponseDto resumed = checkoutService.buildResumeModel(user.getUsername(), matchId);
        if (resumed != null) {
            model.addAttribute("reserved", resumed);
            model.addAttribute("payRequest", new PayRequestDto());
            return "checkout";
        }

        List<StadiumSectionEntity> sections = checkoutService.getSectionsForMatch(matchId);
        Map<UUID, Long> availability = checkoutService.getAvailabilityBySection(matchId);

        Map<String, List<StadiumSectionEntity>> sectionsByStand = sections.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getStandName() == null ? "Sections" : s.getStandName(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        model.addAttribute("sectionsByStand", sectionsByStand);
        model.addAttribute("availability", availability);

        List<TicketTierEntity> tiers = ticketTierRepository.findAll();
        model.addAttribute("tiers", tiers);

        ReserveSectionRequestDto rr = new ReserveSectionRequestDto();
        rr.setMatchId(matchId);
        model.addAttribute("reserveRequest", rr);

        model.addAttribute("payRequest", new PayRequestDto());

        return "checkout";
    }

    @PostMapping("/checkout/reserve")
    public String reserve(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute("reserveRequest") ReserveSectionRequestDto req,
            RedirectAttributes redirectAttributes
    ) {
        ReserveSectionResponseDto resp = checkoutService.reserveSection(userDetails.getUsername(), req);

        redirectAttributes.addFlashAttribute("reserved", resp);
        return "redirect:/checkout/" + resp.getMatchId();
    }

    @PostMapping("/checkout/pay")
    public String pay(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute("payRequest") PayRequestDto req,
            RedirectAttributes redirectAttributes
    ) {
        UUID invoiceId = checkoutService.pay(userDetails.getUsername(), req);
        redirectAttributes.addFlashAttribute("paidInvoiceId", invoiceId.toString());
        return "redirect:/my-tickets";
    }

    @PostMapping("/checkout/cancel")
    public String cancel(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam UUID matchId
    ) {
        checkoutService.cancelReservedForMatch(userDetails.getUsername(), matchId);
        return "redirect:/home";
    }
}
