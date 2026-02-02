package com.football.ticketsale.controller;

import com.football.ticketsale.dto.geo.CityDto;
import com.football.ticketsale.dto.geo.CountryDto;
import com.football.ticketsale.service.GeoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/geo")
public class GeoController {

    private final GeoService geoService;

    public GeoController(GeoService geoService) {
        this.geoService = geoService;
    }

    @GetMapping("/countries")
    public List<CountryDto> countries() {
        return geoService.getCountries();
    }

    @GetMapping("/cities")
    public List<CityDto> cities(@RequestParam UUID countryId) {
        return geoService.getCitiesByCountry(countryId);
    }
}
