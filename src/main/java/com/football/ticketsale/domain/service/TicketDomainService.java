package com.football.ticketsale.domain.service;

import com.football.ticketsale.entity.InvoiceEntity;
import com.football.ticketsale.entity.TicketEntity;
import com.football.ticketsale.entity.UserEntity;
import com.football.ticketsale.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TicketDomainService {

    private final TicketRepository ticketRepository;

    public TicketDomainService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Optional<TicketEntity> findById(UUID ticketId) {
        return ticketRepository.findById(ticketId);
    }

    public List<TicketEntity> findByUser(UserEntity user) {
        return ticketRepository.findByUserEntity(user);
    }

    public List<TicketEntity> findByUserAndStatus(UserEntity user, String status) {
        return ticketRepository.findByUserEntityAndStatus(user, status);
    }

    public List<TicketEntity> findActiveReservedTickets(UserEntity user, String status, LocalDateTime now) {
        return ticketRepository.findByUserEntityAndStatusAndInvoiceEntityIsNullAndReservedUntilAfter(user, status, now);
    }

    public List<TicketEntity> findByInvoice(InvoiceEntity invoice) {
        return ticketRepository.findByInvoiceEntity(invoice);
    }

    public long countActiveReservations(UserEntity user, LocalDateTime now) {
        return ticketRepository.countActiveReservations(user, now);
    }

    public List<TicketEntity> findReservedTicketsForUserAndMatch(UUID userId, UUID matchId, String status, LocalDateTime now) {
        return ticketRepository.findReservedTicketsForUserAndMatch(userId, matchId, status, now);
    }

    public List<TicketEntity> findByUserStatusNoInvoiceMatch(UserEntity user, String status, UUID matchId) {
        return ticketRepository.findByUserEntityAndStatusAndInvoiceEntityIsNullAndMatchId(user, status, matchId);
    }

    public List<TicketEntity> findByUserStatusNoInvoiceMatch(UserEntity user, String status, UUID matchId, LocalDateTime now) {
        return ticketRepository.findReservedTicketsForUserAndMatch(user.getUserUid(), matchId, status, now);
    }

    public List<TicketEntity> findReservedBatch(UUID userUid, UUID matchId, String sectionCode, LocalDateTime reservedUntil, String status) {
        return ticketRepository.findReservedBatch(userUid, matchId, sectionCode, reservedUntil, status);
    }

    public List<TicketEntity> findActiveReservedTicketsForUserAndMatch(String username, UUID matchId, String status, LocalDateTime now) {
        return ticketRepository.findActiveReservedTicketsForUserAndMatch(username, matchId, status, now);
    }

    @Transactional
    public TicketEntity save(TicketEntity ticket) {
        return ticketRepository.save(ticket);
    }

    @Transactional
    public void delete(TicketEntity ticket) {
        ticketRepository.delete(ticket);
    }

    @Transactional
    public List<TicketEntity> findAllByIdForUpdate(List<UUID> ids) {
        return ticketRepository.findAllByIdForUpdate(ids);
    }
}
