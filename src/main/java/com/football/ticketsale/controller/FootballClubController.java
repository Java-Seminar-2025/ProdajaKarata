package com.football.ticketsale.controller;

import com.football.ticketsale.dto.api.FootballClubDto;
import com.football.ticketsale.service.FootballClubService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
public class FootballClubController {

    private final FootballClubService footballClubService;

    public FootballClubController(FootballClubService footballClubService) {
        this.footballClubService = footballClubService;
    }

    @GetMapping
    public List<FootballClubDto> getAll() {
        return footballClubService.getAllClubs();
    }

    @PostMapping
    public FootballClubDto create(@RequestBody FootballClubDto dto) {
        return footballClubService.createClub(dto);
    }
}
