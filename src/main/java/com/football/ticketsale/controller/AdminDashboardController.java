package com.football.ticketsale.controller;

import com.football.ticketsale.dto.admin.CreateMatchForm;
import com.football.ticketsale.service.AdminDashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        var dto = adminDashboardService.loadDashboard();

        model.addAttribute("matchCount", dto.matchCount());
        model.addAttribute("userCount", dto.userCount());
        model.addAttribute("adminCount", dto.adminCount());
        model.addAttribute("users", dto.users());
        model.addAttribute("clubs", dto.clubs());
        model.addAttribute("stadiums", dto.stadiums());
        model.addAttribute("createMatchForm", dto.createMatchForm());

        return "admin/dashboard";
    }

    @GetMapping("/users/{userId}/tickets-view")
    public String openUserTicketPage(@PathVariable UUID userId, Model model) {
        var dto = adminDashboardService.loadUserTickets(userId);
        model.addAttribute("user", dto.user());
        model.addAttribute("tickets", dto.tickets());
        return "admin/user_tickets";
    }

    @PostMapping("/tickets/delete/{ticketId}")
    public String deleteUserTicket(@PathVariable UUID ticketId) {
        UUID userId = adminDashboardService.refundOrCancelTicket(ticketId);
        return "redirect:/admin/users/" + userId + "/tickets-view?success";
    }

    @GetMapping("/invoices")
    public String viewAllInvoices(Model model) {
        var dto = adminDashboardService.loadInvoices();

        model.addAttribute("invoices", dto.invoices());
        model.addAttribute("invoiceCount", dto.invoiceCount());
        model.addAttribute("totalRevenue", dto.totalRevenue());

        return "admin/invoices";
    }

    @PostMapping("/sync-matches")
    public String syncMatches() {
        adminDashboardService.syncMatches();
        return "redirect:/admin/dashboard?sync=success";
    }

    @PostMapping("/promote/{userId}")
    public String promoteUser(@PathVariable UUID userId) {
        adminDashboardService.promoteUser(userId);
        return "redirect:/admin/dashboard?success=User promoted successfully!";
    }

    @PostMapping("/matches/create")
    public String createMatch(@ModelAttribute CreateMatchForm form) {
        adminDashboardService.createMatch(form);
        return "redirect:/admin/dashboard?matchCreated=success";
    }
}
