package com.football.ticketsale.mapper;

import com.football.ticketsale.dto.geo.CityDto;
import com.football.ticketsale.dto.geo.CountryDto;
import com.football.ticketsale.entity.CityEntity;
import com.football.ticketsale.entity.CountryEntity;
import org.springframework.stereotype.Component;

@Component
public class GeoMapper {

    public CountryDto toCountryDto(CountryEntity e) {
        if (e == null) return null;
        return new CountryDto(e.getCountryUid(), e.getCountryName());
    }

    public CityDto toCityDto(CityEntity e) {
        if (e == null) return null;
        return new CityDto(e.getId(), e.getCityName());
    }
}
