package com.football.ticketsale.repository;

import com.football.ticketsale.entity.CityEntity;
import com.football.ticketsale.entity.StadiumEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StadiumRepository extends JpaRepository<StadiumEntity, UUID> {
    Optional<StadiumEntity> findByStadiumName(String stadiumName);
    List<StadiumEntity> findByCityEntity(CityEntity cityEntity);
}
