package com.football.ticketsale.repository;

import com.football.ticketsale.entity.CityEntity;
import com.football.ticketsale.entity.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, String> {
    List<CityEntity> findByCountry(CountryEntity countryEntity);
    Optional<CityEntity> findByCityName(String cityName);
}