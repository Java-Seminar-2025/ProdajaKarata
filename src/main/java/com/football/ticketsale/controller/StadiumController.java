package com.football.ticketsale.controller;

import com.football.ticketsale.entity.StadiumEntity;
import com.football.ticketsale.repository.StadiumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stadiums")
public class StadiumController {

    @Autowired
    private StadiumRepository stadiumRepository;

    @GetMapping
    public List<StadiumEntity> getAllStadiums() {
        return stadiumRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<StadiumEntity> createStadium(@RequestBody StadiumEntity stadium) {
        StadiumEntity savedStadium = stadiumRepository.save(stadium);
        return ResponseEntity.ok(savedStadium);
    }
}