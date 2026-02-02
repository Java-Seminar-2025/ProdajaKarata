package com.football.ticketsale.domain.service;

import com.football.ticketsale.entity.CityEntity;
import com.football.ticketsale.entity.StadiumEntity;
import com.football.ticketsale.repository.StadiumRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class StadiumDomainService {

    private final StadiumRepository stadiumRepository;

    public StadiumDomainService(StadiumRepository stadiumRepository) {
        this.stadiumRepository = stadiumRepository;
    }

    public List<StadiumEntity> findAll() {
        return stadiumRepository.findAll();
    }

    public Optional<StadiumEntity> findById(UUID id) {
        return stadiumRepository.findById(id);
    }

    public Optional<StadiumEntity> findByStadiumName(String stadiumName) {
        return stadiumRepository.findByStadiumName(stadiumName);
    }

    public List<StadiumEntity> findByCity(CityEntity city) {
        return stadiumRepository.findByCityEntity(city);
    }

    @Transactional
    public StadiumEntity save(StadiumEntity stadium) {
        return stadiumRepository.save(stadium);
    }
}
