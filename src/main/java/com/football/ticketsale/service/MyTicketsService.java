package com.football.ticketsale.service;

import com.football.ticketsale.domain.service.SeatReservationDomainService;
import com.football.ticketsale.domain.service.TicketDomainService;
import com.football.ticketsale.domain.service.UserDomainService;
import com.football.ticketsale.dto.view.ActiveReservationDto;
import com.football.ticketsale.dto.view.MyTicketsPageDto;
import com.football.ticketsale.dto.view.TicketViewDto;
import com.football.ticketsale.entity.SeatReservationEntity;
import com.football.ticketsale.entity.TicketEntity;
import com.football.ticketsale.entity.UserEntity;
import com.football.ticketsale.mapper.TicketViewMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MyTicketsService {

    private final UserDomainService userDomainService;
    private final TicketDomainService ticketDomainService;
    private final SeatReservationDomainService seatReservationDomainService;

    public MyTicketsService(
            UserDomainService userDomainService,
            TicketDomainService ticketDomainService,
            SeatReservationDomainService seatReservationDomainService
    ) {
        this.userDomainService = userDomainService;
        this.ticketDomainService = ticketDomainService;
        this.seatReservationDomainService = seatReservationDomainService;
    }

    @Transactional(readOnly = true)
    public MyTicketsPageDto buildMyTicketsPage(String username) {
        UserEntity user = userDomainService.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String role = (user.getAuthorizationLevel() != null && user.getAuthorizationLevel().equalsIgnoreCase("admin"))
                ? "Admin"
                : "User";

        LocalDateTime now = LocalDateTime.now();

        List<TicketEntity> paidTickets = ticketDomainService.findByUserAndStatus(user, TicketEntity.STATUS_PAID);

        List<TicketViewDto> ticketDtos = paidTickets.stream()
                .map(t -> {
                    Optional<SeatReservationEntity> srOpt = seatReservationDomainService.findByTicket(t);

                    Integer seatNumber = srOpt.map(SeatReservationEntity::getSeatNumber).orElse(null);
                    String sectionCode = t.getSectionCode();

                    String invoiceUid = (t.getInvoiceEntity() != null)
                            ? String.valueOf(t.getInvoiceEntity().getInvoiceUid())
                            : null;

                    boolean refundable = srOpt
                            .map(SeatReservationEntity::getMatch)
                            .map(m -> m.getMatchDatetime() != null && !m.getMatchDatetime().isBefore(now.plusDays(3)))
                            .orElse(false);

                    return TicketViewMapper.toDto(t, seatNumber, sectionCode, invoiceUid, refundable);
                })
                .collect(Collectors.toList());

        List<TicketEntity> activeReservations = ticketDomainService.findActiveReservedTickets(user, TicketEntity.STATUS_RESERVED, now);

        record ReservationKey(UUID matchId, String sectionCode, LocalDateTime reservedUntil) {}

        Map<ReservationKey, List<TicketEntity>> grouped = new LinkedHashMap<>();
        for (TicketEntity t : activeReservations) {
            UUID matchId = seatReservationDomainService.findByTicket(t)
                    .map(sr -> sr.getMatch().getMatchUid())
                    .orElse(null);

            ReservationKey key = new ReservationKey(matchId, t.getSectionCode(), t.getReservedUntil());
            grouped.computeIfAbsent(key, __ -> new ArrayList<>()).add(t);
        }

        List<ActiveReservationDto> activeReservationDtos = grouped.entrySet().stream()
                .map(e -> {
                    ReservationKey k = e.getKey();
                    List<TicketEntity> batch = e.getValue();

                    String ownerName = batch.isEmpty() ? null : batch.get(0).getOwnerName();
                    return new ActiveReservationDto(
                            k.matchId,
                            k.sectionCode,
                            batch.size(),
                            ownerName,
                            k.reservedUntil
                    );
                })
                .collect(Collectors.toList());

        return new MyTicketsPageDto(user.getUsername(), role, activeReservationDtos, ticketDtos);
    }

    @Transactional(readOnly = true)
    public MyTicketsPageDto buildPage(String username) {
        return buildMyTicketsPage(username);
    }
}
