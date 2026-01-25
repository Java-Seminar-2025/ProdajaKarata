package com.football.ticketsale.repository;

import com.football.ticketsale.entity.FootballClubEntity;
import com.football.ticketsale.entity.MatchEntity;
import com.football.ticketsale.entity.StadiumEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, UUID> {
    List<MatchEntity> findByStadium(StadiumEntity stadium);
    List<MatchEntity> findByMatchDatetimeBetween(
            java.time.LocalDateTime start,
            java.time.LocalDateTime end
    );
    List<MatchEntity> findByHomeTeamOrAwayTeam(FootballClubEntity homeTeam, FootballClubEntity awayTeam); // moze se napisat ka query da se dva puta ne pise tipa hajduk, hajduk za dohvatit sve utakmice
}