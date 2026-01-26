package com.football.ticketsale.controller;

import com.football.ticketsale.entity.FootballClubEntity;
import com.football.ticketsale.repository.FootballClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
public class FootballClubController {

    @Autowired
    private FootballClubRepository footballClubRepository;

    @GetMapping
    public List<FootballClubEntity> getAllClubs() {
        return footballClubRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<FootballClubEntity> createClub(@RequestBody FootballClubEntity club) {
        FootballClubEntity savedClub = footballClubRepository.save(club);
        return ResponseEntity.ok(savedClub);
    }
}