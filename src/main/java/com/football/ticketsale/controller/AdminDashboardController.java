package com.football.ticketsale.controller;

import com.football.ticketsale.dto.admin.CreateMatchForm;
import com.football.ticketsale.entity.InvoiceEntity;
import com.football.ticketsale.entity.TicketEntity;
import com.football.ticketsale.entity.UserEntity;
import com.football.ticketsale.repository.*;
import com.football.ticketsale.service.MatchService;
import com.football.ticketsale.service.MatchSyncService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminDashboardController {

    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final FootballClubRepository clubRepository;
    private final StadiumRepository stadiumRepository;
    private final MatchSyncService matchSyncService;
    private final MatchService matchService;
    private final TicketRepository ticketRepository;

    // wip dio
    private final InvoiceRepository invoiceRepository;

    public AdminDashboardController(
            MatchRepository matchRepository,
            UserRepository userRepository,
            FootballClubRepository clubRepository,
            StadiumRepository stadiumRepository,
            MatchSyncService matchSyncService,
            MatchService matchService, TicketRepository ticketRepository, InvoiceRepository invoiceRepository
    ) {
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
        this.clubRepository = clubRepository;
        this.stadiumRepository = stadiumRepository;
        this.matchSyncService = matchSyncService;
        this.matchService = matchService;
        this.ticketRepository = ticketRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("matchCount", matchRepository.count());
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("adminCount",
                userRepository.countByAuthorizationLevel("admin"));
        model.addAttribute("adminCount", userRepository.countByAuthorizationLevel("admin"));
        model.addAttribute("users", userRepository.findAll());


        model.addAttribute("clubs", clubRepository.findAll());
        model.addAttribute("stadiums", stadiumRepository.findAll());
        model.addAttribute("createMatchForm", new CreateMatchForm());
        return "admin/dashboard";
    }

    @GetMapping("/users/{userId}/tickets-view")
    public String openUserTicketPage(@PathVariable UUID userId, Model model) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));
        List<TicketEntity> tickets = ticketRepository.findByUserEntity(user);
        model.addAttribute("user", user);
        model.addAttribute("tickets", tickets);
        return "admin/user_tickets";
    }

    @PostMapping("/tickets/delete/{ticketId}")
    public String deleteUserTicket(@PathVariable UUID ticketId) {
        TicketEntity ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("krivi id"));
        UUID userId = ticket.getUserEntity().getUserUid();
        ticketRepository.delete(ticket);
        return "redirect:/admin/users/" + userId + "/tickets-view?success";
    }

    @GetMapping("/invoices")
    public String viewAllInvoices(Model model) {
        List<InvoiceEntity> invoices = invoiceRepository.findAll();

        BigDecimal totalRevenue = invoices.stream()
                        .map(InvoiceEntity::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("invoices", invoices);
        model.addAttribute("invoiceCount", invoiceRepository.count());

        model.addAttribute("totalRevenue", totalRevenue);

        return "admin/invoices";
    }

    @PostMapping("/sync-matches")
    public String syncMatches() {
        matchSyncService.syncUpcoming();
        return "redirect:/admin/dashboard?sync=success";
    }

    @PostMapping("/promote/{userId}")
    public String promoteUser(@PathVariable UUID userId) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user != null && !"admin".equalsIgnoreCase(user.getAuthorizationLevel())) {
            user.setAuthorizationLevel("admin");
            userRepository.save(user);
        }
        return "redirect:/admin/dashboard?success=User promoted successfully!";
    }

    // ovo
    @PostMapping("matches/create")
    public String createMatch(@ModelAttribute CreateMatchForm form) {


        matchService.createMatch(
                form.getHomeClubId(),
                form.getAwayClubId(),
                form.getStadiumId(),
                form.getMatchDateTime(),
                form.getPrice(),

                form.getCompetitionCode(),
                form.getStatus()
        );


        return "redirect:/admin/dashboard?matchCreated=success";
    }
}