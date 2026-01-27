package com.football.ticketsale.controller;

import com.football.ticketsale.service.CheckoutService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
public class RefundController {

    private final CheckoutService checkoutService;

    public RefundController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/refund")
    public String refundTicket(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("ticketId") UUID ticketId,
            RedirectAttributes ra
    ) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        try {
            checkoutService.refundTicket(userDetails.getUsername(), ticketId);
            ra.addFlashAttribute("success", "Refund successful.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        } catch (SecurityException e) {
            ra.addFlashAttribute("error", "You are not allowed to refund this ticket.");
        } catch (EntityNotFoundException e) {
            ra.addFlashAttribute("error", "Ticket not found.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Refund failed. Please try again.");
        }

        return "redirect:/my-tickets";
    }
}
