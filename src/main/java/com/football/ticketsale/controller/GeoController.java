package com.football.ticketsale.controller;

import com.football.ticketsale.entity.CityEntity;
import com.football.ticketsale.entity.CountryEntity;
import com.football.ticketsale.repository.CityRepository;
import com.football.ticketsale.repository.CountryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/geo")
public class GeoController {

    private final CityRepository cityRepo;
    private final CountryRepository countryRepo;

    public GeoController(CityRepository cityRepo, CountryRepository countryRepo) {
        this.cityRepo = cityRepo;
        this.countryRepo = countryRepo;
    }

    @GetMapping("/cities")
    public List<CityItem> cities(@RequestParam UUID countryId) {
        CountryEntity c = countryRepo.findById(countryId).orElseThrow();
        return cityRepo.findByCountry(c).stream()
                .map(x -> new CityItem(x.getId(), x.getCityName()))
                .toList();
    }

    public record CityItem(UUID id, String name) {}
}
