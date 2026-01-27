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

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.stream.Collectors;
import com.football.ticketsale.dto.MatchFilterDto;
import com.football.ticketsale.repository.spec.MatchSpecifications;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


@Service
public class MatchService {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private FootballClubRepository footballClubRepository;

    @Autowired
    private StadiumRepository stadiumRepository;


    // ovo minja za fix admin kreiranje karata
    public MatchEntity createMatch(UUID homeClubId, UUID awayClubId, UUID stadiumId,
                                   LocalDateTime matchTime, BigDecimal price,
                                   String competitionCode, String status) {

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

        // ovo
        match.setCompetitionCode(competitionCode);
        match.setStatus(status);

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

    public List<MatchEntity> getUpcomingMatches(String q) {
        List<MatchEntity> matches = matchRepository
                .findByMatchDatetimeAfterOrderByMatchDatetimeAsc(LocalDateTime.now());

        if (q == null || q.trim().isEmpty()) {
            return matches.stream().limit(30).toList();
        }

        String needle = q.trim().toLowerCase(Locale.ROOT);
        return matches.stream()
                .filter(m ->
                        (m.getHomeTeam() != null && m.getHomeTeam().getClubName() != null && m.getHomeTeam().getClubName().toLowerCase(Locale.ROOT).contains(needle))
                                || (m.getAwayTeam() != null && m.getAwayTeam().getClubName() != null && m.getAwayTeam().getClubName().toLowerCase(Locale.ROOT).contains(needle))
                                || (m.getCompetitionCode() != null && m.getCompetitionCode().toLowerCase(Locale.ROOT).contains(needle))
                )
                .limit(30)
                .toList();
    }
    public List<MatchEntity> getUpcomingMatches(MatchFilterDto filter) {
        var sort = Sort.by(Sort.Direction.ASC, "matchDatetime");
        var pageable = PageRequest.of(0, 30, sort);

        return matchRepository
                .findAll(MatchSpecifications.byFilter(filter), pageable)
                .getContent();
    }

    public List<String> getCompetitionOptions() {
        return matchRepository.findDistinctCompetitionCodes();
    }

}