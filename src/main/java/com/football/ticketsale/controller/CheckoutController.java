package com.football.ticketsale.controller;

import com.football.ticketsale.dto.checkout.CheckoutPageDto;
import com.football.ticketsale.dto.checkout.PayRequestDto;
import com.football.ticketsale.dto.checkout.ReserveSectionRequestDto;
import com.football.ticketsale.service.CheckoutService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @GetMapping("/{matchId}")
    public String checkoutPage(
            @PathVariable UUID matchId,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        CheckoutPageDto dto = checkoutService.buildCheckoutPage(userDetails.getUsername(), matchId);

        model.addAttribute("user", dto.user());
        model.addAttribute("match", dto.match());
        model.addAttribute("reserved", dto.reserved());
        model.addAttribute("reserveRequest", dto.reserveRequest());
        model.addAttribute("payRequest", dto.payRequest());
        model.addAttribute("tiers", dto.tiers());

        if (dto.sectionsByStand() != null) {
            model.addAttribute("sectionsByStand", dto.sectionsByStand());
        }
        if (dto.availability() != null) {
            model.addAttribute("availability", dto.availability());
        }

        return "checkout";
    }

    @PostMapping("/reserve")
    public String reserve(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute ReserveSectionRequestDto req
    ) {
        checkoutService.reserveSection(userDetails.getUsername(), req);
        return "redirect:/checkout/" + req.getMatchId();
    }

    
    @PostMapping("/resume")
    public String resume(@RequestParam UUID matchId) {
        return "redirect:/checkout/" + matchId;
    }

@PostMapping("/pay")
    public String pay(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute PayRequestDto req
    ) {
        checkoutService.pay(userDetails.getUsername(), req);
        return "redirect:/my-tickets?success";
    }

    @PostMapping("/cancel/{matchId}")
    public String cancel(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID matchId) {
        checkoutService.cancelReservedForMatch(userDetails.getUsername(), matchId);
        return "redirect:/checkout/" + matchId + "?cancelled";
    }
}
