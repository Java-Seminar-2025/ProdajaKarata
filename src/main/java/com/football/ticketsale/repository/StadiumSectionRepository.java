package com.football.ticketsale.repository;

import com.football.ticketsale.entity.StadiumEntity;
import com.football.ticketsale.entity.StadiumSectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StadiumSectionRepository extends JpaRepository<StadiumSectionEntity, UUID> {
    List<StadiumSectionEntity> findByStadiumOrderByStandNameAscSectionCodeAsc(StadiumEntity stadium);
    Optional<StadiumSectionEntity> findByStadiumAndSectionCode(StadiumEntity stadium, String sectionCode);
}
