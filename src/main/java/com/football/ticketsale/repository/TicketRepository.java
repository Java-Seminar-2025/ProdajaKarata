package com.football.ticketsale.repository;

import com.football.ticketsale.entity.InvoiceEntity;
import com.football.ticketsale.entity.TicketEntity;
import com.football.ticketsale.entity.TicketTierEntity;
import com.football.ticketsale.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;


@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, UUID> {
    List<TicketEntity> findByUserEntity(UserEntity userEntity);
    List<TicketEntity> findByStatus(String status);
    List<TicketEntity> findByInvoiceEntity(InvoiceEntity invoiceEntity);
    List<TicketEntity> findByTierEntity(TicketTierEntity tierEntity);
    List<TicketEntity> findByUserEntityAndStatus(UserEntity userEntity, String status);
    List<TicketEntity> findByStatusAndReservedUntilBefore(String status, LocalDateTime time);
    List<TicketEntity> findByUserEntityAndStatusAndReservedUntilAfter(UserEntity user, String status, LocalDateTime now);
    List<TicketEntity> findByUserEntityAndStatusAndInvoiceEntityIsNullAndReservedUntilAfter(UserEntity user, String status, LocalDateTime now);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TicketEntity t where t.ticketUid in :ids")
    List<TicketEntity> findAllByIdForUpdate(@Param("ids") List<UUID> ids);

    @Query("select t from TicketEntity t where t.status = :status and t.reservedUntil < :now")
    List<TicketEntity> findExpiredReserved(@Param("status") String status, @Param("now") LocalDateTime now);

    @Query("""
            select t
            from TicketEntity t
            join SeatReservationEntity sr on sr.ticket = t
            where t.userEntity = :user
              and t.status = :status
              and t.invoiceEntity is null
              and sr.match.matchUid = :matchId
            """)
    List<TicketEntity> findByUserEntityAndStatusAndInvoiceEntityIsNullAndMatchId(
            UserEntity user, String status, UUID matchId);

    @Query("""
            select count(t) from TicketEntity t
            where t.userEntity = :user
              and t.status = 'RESERVED'
              and t.invoiceEntity is null
              and t.reservedUntil > :now
            """)
    long countActiveReservations(@Param("user") UserEntity user,
                                 @Param("now") LocalDateTime now);

    @Query("""
                select t
                from SeatReservationEntity sr
                join sr.ticket t
                where sr.match.matchUid = :matchId
                  and t.userEntity.username = :username
                  and t.status = :status
                  and t.invoiceEntity is null
                  and t.reservedUntil > :now
                order by t.creationDatetime asc
            """)
    List<TicketEntity> findActiveReservedTicketsForUserAndMatch(
            @Param("username") String username,
            @Param("matchId") UUID matchId,
            @Param("status") String status,
            @Param("now") LocalDateTime now
    );

    @Query("""
                select t
                from SeatReservationEntity sr
                join sr.ticket t
                where t.userEntity.userUid = :userUid
                  and sr.match.matchUid = :matchId
                  and t.status = :status
                  and t.invoiceEntity is null
                  and t.reservedUntil > :now
                order by t.creationDatetime asc
            """)
    List<TicketEntity> findReservedTicketsForUserAndMatch(
            @Param("userUid") UUID userUid,
            @Param("matchId") UUID matchId,
            @Param("status") String status,
            @Param("now") LocalDateTime now
    );

    @Query("""
              select t from SeatReservationEntity sr
              join sr.ticket t
              where t.userEntity.userUid = :userUid
                and sr.match.matchUid = :matchId
                and t.sectionCode = :sectionCode
                and t.reservedUntil = :reservedUntil
                and t.status = :status
                and t.invoiceEntity is null
            """)
    List<TicketEntity> findReservedBatch(
            @Param("userUid") UUID userUid,
            @Param("matchId") UUID matchId,
            @Param("sectionCode") String sectionCode,
            @Param("reservedUntil") LocalDateTime reservedUntil,
            @Param("status") String status
    );


}

