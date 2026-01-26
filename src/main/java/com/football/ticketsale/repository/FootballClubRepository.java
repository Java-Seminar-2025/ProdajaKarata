package com.football.ticketsale.repository;

import com.football.ticketsale.entity.FootballClubEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FootballClubRepository extends JpaRepository<FootballClubEntity, UUID> {
    Optional<FootballClubEntity> findByClubName(String clubName);
    boolean existsByClubName(String clubName);
    Optional<FootballClubEntity> findBySourceAndSourceTeamId(String source, String sourceTeamId);
}