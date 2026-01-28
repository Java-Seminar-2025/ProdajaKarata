package com.football.ticketsale.repository;

import com.football.ticketsale.entity.TicketTierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketTierRepository extends JpaRepository<TicketTierEntity, UUID> {
    Optional<TicketTierEntity> findByTierNameIgnoreCase(String tierName);
}

