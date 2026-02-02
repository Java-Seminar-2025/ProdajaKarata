package com.football.ticketsale.service;

import com.football.ticketsale.domain.service.FootballClubDomainService;
import com.football.ticketsale.dto.api.FootballClubDto;
import com.football.ticketsale.mapper.FootballClubMapper;
import com.football.ticketsale.entity.FootballClubEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FootballClubService {

    private final FootballClubDomainService clubDomainService;

    public FootballClubService(FootballClubDomainService clubDomainService) {
        this.clubDomainService = clubDomainService;
    }

    @Transactional(readOnly = true)
    public List<FootballClubDto> getAllClubs() {
        return clubDomainService.findAll().stream()
                .map(FootballClubMapper::toDto)
                .toList();
    }

    @Transactional
    public FootballClubDto createClub(FootballClubDto req) {
        FootballClubEntity e = new FootballClubEntity();
        e.setClubName(req.getClubName());
        e.setSource(req.getSource());
        e.setTotalPlayers(req.getTotalPlayers() != null ? req.getTotalPlayers() : 0);
        e = clubDomainService.save(e);
        return FootballClubMapper.toDto(e);
    }
}
