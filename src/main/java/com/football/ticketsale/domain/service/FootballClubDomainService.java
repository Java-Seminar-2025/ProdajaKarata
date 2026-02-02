package com.football.ticketsale.domain.service;

import com.football.ticketsale.entity.FootballClubEntity;
import com.football.ticketsale.repository.FootballClubRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class FootballClubDomainService {

    private final FootballClubRepository footballClubRepository;

    public FootballClubDomainService(FootballClubRepository footballClubRepository) {
        this.footballClubRepository = footballClubRepository;
    }

    public List<FootballClubEntity> findAll() {
        return footballClubRepository.findAll();
    }

    public Optional<FootballClubEntity> findById(UUID id) {
        return footballClubRepository.findById(id);
    }

    public Optional<FootballClubEntity> findByClubName(String clubName) {
        return footballClubRepository.findByClubName(clubName);
    }

    public boolean existsByClubName(String clubName) {
        return footballClubRepository.existsByClubName(clubName);
    }

    public Optional<FootballClubEntity> findBySourceAndSourceTeamId(String source, String sourceTeamId) {
        return footballClubRepository.findBySourceAndSourceTeamId(source, sourceTeamId);
    }

    @Transactional
    public FootballClubEntity save(FootballClubEntity club) {
        return footballClubRepository.save(club);
    }
}
