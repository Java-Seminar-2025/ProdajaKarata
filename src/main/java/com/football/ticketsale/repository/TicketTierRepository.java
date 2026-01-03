package com.football.ticketsale.repository;

import com.football.ticketsale.entity.TicketTierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketTierRepository extends JpaRepository<TicketTierEntity, String> {

}