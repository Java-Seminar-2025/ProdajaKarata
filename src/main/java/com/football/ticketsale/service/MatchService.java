package com.football.ticketsale.service;

import com.football.ticketsale.domain.service.FootballClubDomainService;
import com.football.ticketsale.domain.service.MatchDomainService;
import com.football.ticketsale.domain.service.StadiumDomainService;
import com.football.ticketsale.dto.MatchFilterDto;
import com.football.ticketsale.dto.admin.CreateMatchForm;
import com.football.ticketsale.entity.FootballClubEntity;
import com.football.ticketsale.entity.MatchEntity;
import com.football.ticketsale.entity.StadiumEntity;
import com.football.ticketsale.repository.spec.MatchSpecifications;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MatchService {

    private final MatchDomainService matchDomainService;
    private final FootballClubDomainService clubDomainService;
    private final StadiumDomainService stadiumDomainService;

    public MatchService(
            MatchDomainService matchDomainService,
            FootballClubDomainService clubDomainService,
            StadiumDomainService stadiumDomainService
    ) {
        this.matchDomainService = matchDomainService;
        this.clubDomainService = clubDomainService;
        this.stadiumDomainService = stadiumDomainService;
    }

    @Transactional(readOnly = true)
    public List<MatchEntity> getUpcomingMatches(int limit) {
        List<MatchEntity> all = matchDomainService.findUpcoming(LocalDateTime.now());
        return all.stream().limit(limit).toList();
    }

    @Transactional(readOnly = true)
    public List<MatchEntity> getMatchesByCompetition(String competitionCode, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return matchDomainService.findByCompetition(competitionCode, pageable);
    }

    @Transactional(readOnly = true)
    public List<String> getCompetitionOptions() {
        return matchDomainService.findDistinctCompetitionCodes();
    }

    @Transactional(readOnly = true)
    public List<MatchEntity> getAllMatches() {
        return matchDomainService.findAll();
    }

    @Transactional(readOnly = true)
    public List<MatchEntity> searchMatches(MatchFilterDto filter) {
        var spec = MatchSpecifications.filterMatches(filter);
        return matchDomainService.findAll(spec, Sort.by(Sort.Direction.ASC, "matchDatetime"));
    }

    @Transactional
    public MatchEntity createMatch(UUID homeClubId, UUID awayClubId, UUID stadiumId, LocalDateTime matchDateTime,
                                   java.math.BigDecimal price, String competitionCode, String status) {

        FootballClubEntity home = clubDomainService.findById(homeClubId)
                .orElseThrow(() -> new EntityNotFoundException("Home club not found"));

        FootballClubEntity away = clubDomainService.findById(awayClubId)
                .orElseThrow(() -> new EntityNotFoundException("Away club not found"));

        StadiumEntity stadium = stadiumDomainService.findById(stadiumId)
                .orElseThrow(() -> new EntityNotFoundException("Stadium not found"));

        MatchEntity match = new MatchEntity();
        match.setHomeTeam(home);
        match.setAwayTeam(away);
        match.setStadium(stadium);
        match.setMatchDatetime(matchDateTime);
        match.setBaseTicketPriceUsd(price);
        match.setCompetitionCode(competitionCode);
        match.setStatus(status);

        return matchDomainService.save(match);
    }

    @Transactional
    public MatchEntity createMatchFromForm(CreateMatchForm form) {
        return createMatch(
                form.getHomeClubId(),
                form.getAwayClubId(),
                form.getStadiumId(),
                form.getMatchDateTime(),
                form.getPrice(),
                form.getCompetitionCode(),
                form.getStatus()
        );
    }

    @Transactional
    public void deleteMatch(UUID matchId) {
        MatchEntity match = matchDomainService.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("Match not found"));
        matchDomainService.delete(match);
    }
}
