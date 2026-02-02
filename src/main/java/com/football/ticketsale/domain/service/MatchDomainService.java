package com.football.ticketsale.domain.service;

import com.football.ticketsale.entity.MatchEntity;
import com.football.ticketsale.repository.MatchRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class MatchDomainService {

    private final MatchRepository matchRepository;

    public MatchDomainService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public Optional<MatchEntity> findById(UUID matchId) {
        return matchRepository.findById(matchId);
    }

    public List<MatchEntity> findAll() {
        return matchRepository.findAll();
    }

    public long count() {
        return matchRepository.count();
    }

    public List<String> findDistinctCompetitionCodes() {
        return matchRepository.findDistinctCompetitionCodes();
    }

    public List<MatchEntity> findByCompetition(String competitionCode, Pageable pageable) {
        return matchRepository.findByCompetitionCodeAndMatchDatetimeAfterOrderByMatchDatetimeAsc(
                competitionCode, LocalDateTime.now(), pageable
        );
    }

    public List<MatchEntity> findUpcoming(LocalDateTime now) {
        return matchRepository.findByMatchDatetimeAfterOrderByMatchDatetimeAsc(now);
    }

    public Optional<MatchEntity> findBySourceAndSourceMatchId(String source, String sourceMatchId) {
        return matchRepository.findBySourceAndSourceMatchId(source, sourceMatchId);
    }

    
    public List<MatchEntity> findAll(org.springframework.data.jpa.domain.Specification<MatchEntity> spec,
                                    org.springframework.data.domain.Sort sort) {
        return matchRepository.findAll(spec, sort);
    }

    @Transactional
    public MatchEntity save(MatchEntity match) {
        return matchRepository.save(match);
    }

    @Transactional
    public void delete(MatchEntity match) {
        matchRepository.delete(match);
    }
}
