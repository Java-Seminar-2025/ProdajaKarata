package com.football.ticketsale.repository;

import com.football.ticketsale.entity.InvoiceEntity;
import com.football.ticketsale.entity.TicketEntity;
import com.football.ticketsale.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, UUID> {
    List<TicketEntity> findByUser(UserEntity user);
    List<TicketEntity> findByStatus(String status);
    List<TicketEntity> findByInvoice(InvoiceEntity invoice);
    List<TicketEntity> findBYTier(TicketEntity tier);
}