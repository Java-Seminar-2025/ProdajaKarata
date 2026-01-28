package com.football.ticketsale.repository;

import com.football.ticketsale.entity.MatchEntity;
import com.football.ticketsale.entity.SeatReservationEntity;
import com.football.ticketsale.entity.SeatReservationId;
import com.football.ticketsale.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface SeatReservationRepository extends JpaRepository<SeatReservationEntity, SeatReservationId> {
    List<SeatReservationEntity> findByMatch(MatchEntity match);
    Optional<SeatReservationEntity> findByTicket(TicketEntity ticket);
    boolean existsByMatchAndSeatNumber(MatchEntity match, Integer seatNumber);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from SeatReservationEntity sr where sr.ticket = :ticket")
    void deleteByTicket(@Param("ticket") TicketEntity ticket);

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


    @Query("""
        select sr.seatNumber
        from SeatReservationEntity sr
        join sr.ticket t
        where sr.match = :match
          and sr.seatNumber between :start and :end
          and t.status = 'RESERVED'
          and t.invoiceEntity is null
          and t.reservedUntil > :now
        order by sr.seatNumber
    """)
    List<Integer> findActiveSeatNumbersInRange(@Param("match") MatchEntity match,
                                               @Param("start") Integer start,
                                               @Param("end") Integer end,
                                               @Param("now") LocalDateTime now);

    @Query("""
        select count(sr)
        from SeatReservationEntity sr
        join sr.ticket t
        where sr.match = :match
          and sr.seatNumber between :start and :end
          and t.status = 'RESERVED'
          and t.invoiceEntity is null
          and t.reservedUntil > :now
    """)
    long countActiveReservedInRange(@Param("match") MatchEntity match,
                                    @Param("start") Integer start,
                                    @Param("end") Integer end,
                                    @Param("now") LocalDateTime now);


    @Query("""
        select sr.seatNumber
        from SeatReservationEntity sr
        join sr.ticket t
        where sr.match = :match
          and sr.seatNumber between :start and :end
          and (
                t.status = 'PAID'
             or (t.status = 'RESERVED' and t.invoiceEntity is null and t.reservedUntil > :now)
          )
        order by sr.seatNumber
    """)
    List<Integer> findTakenSeatNumbersInRange(@Param("match") MatchEntity match,
                                              @Param("start") Integer start,
                                              @Param("end") Integer end,
                                              @Param("now") LocalDateTime now);

    @Query("""
        select count(sr)
        from SeatReservationEntity sr
        join sr.ticket t
        where sr.match = :match
          and sr.seatNumber between :start and :end
          and (
                t.status = 'PAID'
             or (t.status = 'RESERVED' and t.invoiceEntity is null and t.reservedUntil > :now)
          )
    """)
    long countTakenInRange(@Param("match") MatchEntity match,
                           @Param("start") Integer start,
                           @Param("end") Integer end,
                           @Param("now") LocalDateTime now);

}
