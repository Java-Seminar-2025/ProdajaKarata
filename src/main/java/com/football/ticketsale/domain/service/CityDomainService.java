package com.football.ticketsale.domain.service;

import com.football.ticketsale.entity.CityEntity;
import com.football.ticketsale.entity.CountryEntity;
import com.football.ticketsale.repository.CityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CityDomainService {

    private final CityRepository cityRepository;

    public CityDomainService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public List<CityEntity> findAll() {
        return cityRepository.findAll();
    }

    public List<CityEntity> findByCountry(CountryEntity country) {
        return cityRepository.findByCountry(country);
    }

    public Optional<CityEntity> findById(UUID id) {
        return cityRepository.findById(id);
    }

    public Optional<CityEntity> findByCityName(String cityName) {
        return cityRepository.findByCityName(cityName);
    }

    @Transactional
    public CityEntity save(CityEntity city) {
        return cityRepository.save(city);
    }
}
