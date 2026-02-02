package com.football.ticketsale.service;

import com.football.ticketsale.domain.service.StadiumDomainService;
import com.football.ticketsale.dto.api.StadiumDto;
import com.football.ticketsale.mapper.StadiumMapper;
import com.football.ticketsale.entity.StadiumEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StadiumService {

    private final StadiumDomainService stadiumDomainService;

    public StadiumService(StadiumDomainService stadiumDomainService) {
        this.stadiumDomainService = stadiumDomainService;
    }

    @Transactional(readOnly = true)
    public List<StadiumDto> getAllStadiums() {
        return stadiumDomainService.findAll().stream()
                .map(StadiumMapper::toDto)
                .toList();
    }

    @Transactional
    public StadiumDto create(StadiumDto dto) {
        StadiumEntity e = new StadiumEntity();
        e.setStadiumName(dto.getStadiumName());
        e.setNumberOfSeats(dto.getNumberOfSeats());
        return StadiumMapper.toDto(stadiumDomainService.save(e));
    }

    @Transactional
    public StadiumDto createStadium(StadiumDto dto) {
        return create(dto);
    }
}
