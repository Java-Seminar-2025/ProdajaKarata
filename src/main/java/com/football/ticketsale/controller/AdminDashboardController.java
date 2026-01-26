package com.football.ticketsale.controller;

import com.football.ticketsale.entity.UserEntity;
import com.football.ticketsale.repository.MatchRepository;
import com.football.ticketsale.repository.UserRepository;
import com.football.ticketsale.service.MatchSyncService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminDashboardController {

    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final MatchSyncService matchSyncService;

    public AdminDashboardController(MatchRepository matchRepository,
                                    UserRepository userRepository,
                                    MatchSyncService matchSyncService) {
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
        this.matchSyncService = matchSyncService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("matchCount", matchRepository.count());
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("adminCount", userRepository.countByAuthorizationLevel("admin"));
        model.addAttribute("users", userRepository.findAll()); // <-- add UserEntity list
        return "admin/dashboard";
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
}