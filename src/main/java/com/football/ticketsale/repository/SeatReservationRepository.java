package com.football.ticketsale.repository;

import com.football.ticketsale.entity.MatchEntity;
import com.football.ticketsale.entity.SeatReservationEntity;
import com.football.ticketsale.entity.SeatReservationId;
import com.football.ticketsale.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface SeatReservationRepository extends JpaRepository<SeatReservationEntity, SeatReservationId> {
    List<SeatReservationEntity> findByMatch(MatchEntity match);
    Optional<SeatReservationEntity> findByTicket(TicketEntity ticket);
    boolean existsByMatchAndSeatNumber(MatchEntity match, Integer seatNumber);

    @Query("select sr.seatNumber from SeatReservationEntity sr " +
            "where sr.match = :match and sr.seatNumber between :start and :end")
    List<Integer> findSeatNumbersInRange(@Param("match") MatchEntity match,
                                         @Param("start") Integer start,
                                         @Param("end") Integer end);

    @Query("select count(sr) from SeatReservationEntity sr " +
            "where sr.match = :match and sr.seatNumber between :start and :end")
    long countReservedInRange(@Param("match") MatchEntity match,
                              @Param("start") Integer start,
                              @Param("end") Integer end);
}

