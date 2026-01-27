package com.football.ticketsale.controller;

import com.football.ticketsale.dto.MatchDto;
import com.football.ticketsale.entity.MatchEntity;
import com.football.ticketsale.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @GetMapping
    public List<MatchEntity> getAllMatches() {
        return matchService.getAllMatches();
    }

    @PostMapping
    public ResponseEntity<MatchEntity> createMatch(@RequestBody MatchDto request) {
        MatchEntity createdMatch = matchService.createMatch(
                request.getHomeClubId(),
                request.getAwayClubId(),
                request.getStadiumId(),
                request.getMatchDateTime(),
                request.getPrice(),
                request.getCompetitionCode(),
                request.getStatus()
        );
        return ResponseEntity.ok(createdMatch);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMatch(@PathVariable UUID id) {
        matchService.deleteMatch(id);
        return ResponseEntity.ok("nema je vise!");
    }
}