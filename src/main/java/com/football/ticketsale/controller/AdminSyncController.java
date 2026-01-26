package com.football.ticketsale.controller;

import com.football.ticketsale.service.MatchSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminSyncController {

    private final MatchSyncService syncService;

    public AdminSyncController(MatchSyncService syncService) {
        this.syncService = syncService;
    }

    @PostMapping("/sync-matches")
    public ResponseEntity<String> syncMatchesNow() {
        int count = syncService.syncUpcoming();
        return ResponseEntity.ok("Upserted matches: " + count);
    }
}
