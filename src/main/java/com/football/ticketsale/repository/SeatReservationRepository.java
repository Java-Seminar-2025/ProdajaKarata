package com.football.ticketsale.repository;

import com.football.ticketsale.entity.MatchEntity;
import com.football.ticketsale.entity.SeatReservationEntity;
import com.football.ticketsale.entity.SeatReservationId;
import com.football.ticketsale.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatReservationRepository extends JpaRepository<SeatReservationEntity, SeatReservationId> {
    List<SeatReservationEntity> findByMatch(MatchEntity match);
    Optional<SeatReservationEntity> findByTicket(TicketEntity ticket);
    boolean existsByMatchAndSeatNumber(MatchEntity match, Integer seatNumber);
}