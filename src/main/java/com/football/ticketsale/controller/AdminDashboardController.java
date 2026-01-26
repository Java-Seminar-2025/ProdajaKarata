package com.football.ticketsale.controller.admin;


import com.football.ticketsale.service.MatchSyncService;
import com.football.ticketsale.repository.MatchRepository;
import com.football.ticketsale.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {


    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final MatchSyncService matchSyncService;


    public AdminDashboardController(
            MatchRepository matchRepository,
            UserRepository userRepository,
            MatchSyncService matchSyncService
    ) {
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
        this.matchSyncService = matchSyncService;
    }


    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("matchCount", matchRepository.count());
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("adminCount",
                userRepository.countByAuthorizationLevel("admin"));
        return "admin/dashboard";
    }


    @PostMapping("/admin/sync-matches")
    public String syncMatches() {
        matchSyncService.syncUpcoming();
        return "redirect:/admin/dashboard?sync=success";
    }
}
