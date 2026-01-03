package com.football.ticketsale.repository;

import com.football.ticketsale.entity.FootballClubEntity;
import com.football.ticketsale.entity.MatchCompetitorEntity;
import com.football.ticketsale.entity.MatchCompetitorId;
import com.football.ticketsale.entity.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchCompetitorRepository extends JpaRepository<MatchCompetitorEntity, MatchCompetitorId> {
    List<MatchCompetitorEntity> findByMatchEntity(MatchEntity match);
    List<MatchCompetitorEntity> findByClubEntity(FootballClubEntity club);
}