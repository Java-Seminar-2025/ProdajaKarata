package com.football.ticketsale.domain.service;

import com.football.ticketsale.entity.CountryEntity;
import com.football.ticketsale.repository.CountryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CountryDomainService {

    private final CountryRepository countryRepository;

    public CountryDomainService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public List<CountryEntity> findAll() {
        return countryRepository.findAll();
    }

    public Optional<CountryEntity> findById(UUID id) {
        return countryRepository.findById(id);
    }

    public Optional<CountryEntity> findByCountryName(String countryName) {
        return countryRepository.findByCountryName(countryName);
    }

    @Transactional
    public CountryEntity save(CountryEntity country) {
        return countryRepository.save(country);
    }
}
