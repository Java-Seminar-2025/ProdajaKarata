package com.football.ticketsale.controller;

import com.football.ticketsale.dto.checkout.PayRequestDto;
import com.football.ticketsale.dto.checkout.ReserveSectionRequestDto;
import com.football.ticketsale.dto.checkout.ReserveSectionResponseDto;
import com.football.ticketsale.entity.MatchEntity;
import com.football.ticketsale.entity.StadiumSectionEntity;
import com.football.ticketsale.repository.MatchRepository;
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

    public CheckoutController(CheckoutService checkoutService, MatchRepository matchRepository) {
        this.checkoutService = checkoutService;
        this.matchRepository = matchRepository;
    }

    @GetMapping("/checkout/{matchId}")
    public String checkoutPage(@PathVariable UUID matchId, Model model) {
        MatchEntity match = matchRepository.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("Match not found"));

        List<StadiumSectionEntity> sections = checkoutService.getSectionsForMatch(matchId);
        Map<UUID, Long> availability = checkoutService.getAvailabilityBySection(matchId);

        Map<String, List<StadiumSectionEntity>> sectionsByStand = sections.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getStandName() == null ? "Sections" : s.getStandName(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        model.addAttribute("match", match);
        model.addAttribute("sectionsByStand", sectionsByStand);
        model.addAttribute("availability", availability);

        model.addAttribute("reserveRequest", new ReserveSectionRequestDto());
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
}
