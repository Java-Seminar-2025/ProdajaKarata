package com.football.ticketsale.service;

import com.football.ticketsale.domain.service.CityDomainService;
import com.football.ticketsale.domain.service.CountryDomainService;
import com.football.ticketsale.dto.geo.CityDto;
import com.football.ticketsale.dto.geo.CountryDto;
import com.football.ticketsale.entity.CountryEntity;
import com.football.ticketsale.mapper.GeoMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class GeoService {

    private final CountryDomainService countryDomainService;
    private final CityDomainService cityDomainService;
    private final GeoMapper geoMapper;

    public GeoService(
            CountryDomainService countryDomainService,
            CityDomainService cityDomainService,
            GeoMapper geoMapper
    ) {
        this.countryDomainService = countryDomainService;
        this.cityDomainService = cityDomainService;
        this.geoMapper = geoMapper;
    }

    @Transactional(readOnly = true)
    public List<CountryDto> getCountries() {
        return countryDomainService.findAll().stream()
                .sorted(Comparator.comparing(CountryEntity::getCountryName, String.CASE_INSENSITIVE_ORDER))
                .map(geoMapper::toCountryDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CityDto> getCitiesByCountry(UUID countryId) {
        CountryEntity country = countryDomainService.findById(countryId)
                .orElseThrow(() -> new EntityNotFoundException("Country not found"));

        return cityDomainService.findByCountry(country).stream()
                .sorted(Comparator.comparing(c -> c.getCityName(), String.CASE_INSENSITIVE_ORDER))
                .map(geoMapper::toCityDto)
                .toList();
    }
}
