package com.football.ticketsale.controller;

import com.football.ticketsale.dto.api.StadiumDto;
import com.football.ticketsale.service.StadiumService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stadiums")
public class StadiumController {

    private final StadiumService stadiumService;

    public StadiumController(StadiumService stadiumService) {
        this.stadiumService = stadiumService;
    }

    @GetMapping
    public List<StadiumDto> listStadiums() {
        return stadiumService.getAllStadiums();
    }

    @PostMapping
    public StadiumDto createStadium(@RequestBody StadiumDto stadium) {
        return stadiumService.createStadium(stadium);
    }
}
