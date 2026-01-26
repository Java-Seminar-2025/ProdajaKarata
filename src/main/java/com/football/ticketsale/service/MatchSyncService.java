package com.football.ticketsale.service;

import com.football.ticketsale.entity.FootballClubEntity;
import com.football.ticketsale.entity.MatchEntity;
import com.football.ticketsale.integration.footballdata.FootballDataClient;
import com.football.ticketsale.integration.footballdata.FootballDataProperties;
import com.football.ticketsale.repository.FootballClubRepository;
import com.football.ticketsale.repository.MatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;

import static com.football.ticketsale.integration.footballdata.FootballDataDtos.*;
import com.football.ticketsale.entity.StadiumEntity;
import com.football.ticketsale.repository.StadiumRepository;


@Service
public class MatchSyncService {

    private static final String SOURCE = "FOOTBALL_DATA";
    private static final BigDecimal DEFAULT_PRICE = new BigDecimal("20.00");

    private final FootballDataClient client;
    private final FootballDataProperties props;
    private final FootballClubRepository clubRepo;
    private final MatchRepository matchRepo;
    private final StadiumRepository stadiumRepo;


    public MatchSyncService(
            FootballDataClient client,
            FootballDataProperties props,
            FootballClubRepository clubRepo,
            MatchRepository matchRepo,
            StadiumRepository stadiumRepo
    ) {
        this.client = client;
        this.props = props;
        this.clubRepo = clubRepo;
        this.matchRepo = matchRepo;
        this.stadiumRepo = stadiumRepo;
    }

    @Transactional
    public int syncUpcoming() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        LocalDate to = today.plusDays(props.getDaysAhead());

        String dateFrom = today.format(DateTimeFormatter.ISO_DATE);
        String dateTo = to.format(DateTimeFormatter.ISO_DATE);

        int upserted = 0;

        for (String competition : props.getCompetitions()) {
            MatchesResponse resp = client.getUpcomingMatches(competition, dateFrom, dateTo);
            if (resp == null || resp.getMatches() == null) continue;

            for (ApiMatch m : resp.getMatches()) {
                FootballClubEntity home = upsertClub(m.getHomeTeam());
                FootballClubEntity away = upsertClub(m.getAwayTeam());

                MatchEntity match = matchRepo
                        .findBySourceAndSourceMatchId(SOURCE, String.valueOf(m.getId()))
                        .orElseGet(MatchEntity::new);

                match.setSource(SOURCE);
                match.setSourceMatchId(String.valueOf(m.getId()));
                match.setCompetitionCode(m.getCompetition() != null ? m.getCompetition().getCode() : null);
                match.setStatus(m.getStatus());

                LocalDateTime dt = OffsetDateTime.parse(m.getUtcDate()).withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();
                match.setMatchDatetime(dt);

                match.setHomeTeam(home);
                match.setAwayTeam(away);

                StadiumEntity stadium = getOrCreateDefaultStadiumForCompetition(
                        match.getCompetitionCode() != null ? match.getCompetitionCode() : competition
                );

                match.setStadium(stadium);


                if (match.getBaseTicketPriceUsd() == null) {
                    match.setBaseTicketPriceUsd(DEFAULT_PRICE);
                }

                matchRepo.save(match);
                upserted++;
            }
        }

        return upserted;
    }

    private FootballClubEntity upsertClub(TeamRef team) {
        if (team == null || team.getId() == null) {
            throw new IllegalStateException("Team data missing from API response");
        }

        return clubRepo.findBySourceAndSourceTeamId(SOURCE, String.valueOf(team.getId()))
                .map(existing -> {
                    String name = team.getName() != null ? team.getName() : existing.getClubName();
                    existing.setClubName(name);
                    return clubRepo.save(existing);
                })
                .orElseGet(() -> {
                    FootballClubEntity club = new FootballClubEntity();
                    club.setSource(SOURCE);
                    club.setSourceTeamId(String.valueOf(team.getId()));
                    club.setClubName(team.getName() != null ? team.getName() : "Team " + team.getId());
                    club.setTotalPlayers(11);
                    return clubRepo.save(club);
                });
    }

    private StadiumEntity getOrCreateDefaultStadiumForCompetition(String competitionCode) {
        String stadiumName = switch (competitionCode) {
            case "BL1" -> "Bundesliga Arena";
            case "PL"  -> "Premier League Stadium";
            case "SA"  -> "Serie A Arena";
            case "PD"  -> "LaLiga Stadium";
            default    -> "Default Stadium (" + competitionCode + ")";
        };

        int capacity = switch (competitionCode) {
            case "BL1", "PL", "PD", "SA" -> 2000;
            default -> 1200;
        };

        return stadiumRepo.findByStadiumName(stadiumName)
                .orElseGet(() -> {
                    StadiumEntity s = new StadiumEntity();
                    s.setStadiumName(stadiumName);
                    s.setNumberOfSeats(capacity);
                    return stadiumRepo.save(s);
                });
    }

}
