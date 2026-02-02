package com.football.ticketsale.domain.service;

import com.football.ticketsale.entity.MatchEntity;
import com.football.ticketsale.entity.StadiumEntity;
import com.football.ticketsale.entity.StadiumSectionEntity;
import com.football.ticketsale.repository.StadiumSectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class StadiumSectionDomainService {

    private final StadiumSectionRepository stadiumSectionRepository;

    public StadiumSectionDomainService(StadiumSectionRepository stadiumSectionRepository) {
        this.stadiumSectionRepository = stadiumSectionRepository;
    }

    public List<StadiumSectionEntity> findByStadiumOrdered(StadiumEntity stadium) {
        return stadiumSectionRepository.findByStadiumOrderByStandNameAscSectionCodeAsc(stadium);
    }

    public Optional<StadiumSectionEntity> findByStadiumAndSectionCode(StadiumEntity stadium, String sectionCode) {
        return stadiumSectionRepository.findByStadiumAndSectionCode(stadium, sectionCode);
    }
}
