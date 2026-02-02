package com.football.ticketsale.repository;

import com.football.ticketsale.entity.FootballClubEntity;
import com.football.ticketsale.entity.MatchEntity;
import com.football.ticketsale.entity.StadiumEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Pageable;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, UUID>, JpaSpecificationExecutor<MatchEntity> {
    List<MatchEntity> findByStadium(StadiumEntity stadium);
    List<MatchEntity> findByMatchDatetimeBetween(
            java.time.LocalDateTime start,
            java.time.LocalDateTime end
    );
    List<MatchEntity> findByHomeTeamOrAwayTeam(FootballClubEntity homeTeam, FootballClubEntity awayTeam); // moze se napisat ka query da se dva puta ne pise tipa hajduk, hajduk za dohvatit sve utakmice
    Optional<MatchEntity> findBySourceAndSourceMatchId(String source, String sourceMatchId);
    List<MatchEntity> findByMatchDatetimeAfterOrderByMatchDatetimeAsc(LocalDateTime from);

    List<MatchEntity> findByCompetitionCodeAndMatchDatetimeAfterOrderByMatchDatetimeAsc(
            String competitionCode,
            LocalDateTime from,
            Pageable pageable
    );

    @Query("select distinct m.competitionCode from MatchEntity m where m.competitionCode is not null order by m.competitionCode")
    List<String> findDistinctCompetitionCodes();

}