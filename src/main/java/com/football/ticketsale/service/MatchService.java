package com.football.ticketsale.service;

import com.football.ticketsale.entity.FootballClubEntity;
import com.football.ticketsale.entity.MatchEntity;
import com.football.ticketsale.entity.StadiumEntity;
import com.football.ticketsale.repository.FootballClubRepository;
import com.football.ticketsale.repository.MatchRepository;
import com.football.ticketsale.repository.StadiumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MatchService {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private FootballClubRepository footballClubRepository;

    @Autowired
    private StadiumRepository stadiumRepository;

    public MatchEntity createMatch(UUID homeClubId, UUID awayClubId, UUID stadiumId,
                                   LocalDateTime matchTime, BigDecimal price) {

        if (homeClubId.equals(awayClubId)) {
            throw new IllegalArgumentException("nemoš igrat protiv sebe");
        }

        FootballClubEntity homeTeam = footballClubRepository.findById(homeClubId)
                .orElseThrow(() -> new RuntimeException("homeTeam ne postoji"));
        FootballClubEntity awayTeam = footballClubRepository.findById(awayClubId)
                .orElseThrow(() -> new RuntimeException("awayTeam ne postoji"));
        StadiumEntity stadium = stadiumRepository.findById(stadiumId)
                .orElseThrow(() -> new RuntimeException("stadium ne postoji"));

        MatchEntity match = new MatchEntity();
        match.setHomeTeam(homeTeam);
        match.setAwayTeam(awayTeam);
        match.setStadium(stadium);
        match.setMatchDatetime(matchTime);
        match.setBaseTicketPriceUsd(price);

        return matchRepository.save(match);
    }

    public List<MatchEntity> getAllMatches() {
        return matchRepository.findAll();
    }

    public List<MatchEntity> getMatchesForClub(UUID clubId) {
        FootballClubEntity club = footballClubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("klub ne postoji"));
        return matchRepository.findByHomeTeamOrAwayTeam(club, club);
    }

    public void deleteMatch(UUID matchId) {
        matchRepository.deleteById(matchId);
    }
}