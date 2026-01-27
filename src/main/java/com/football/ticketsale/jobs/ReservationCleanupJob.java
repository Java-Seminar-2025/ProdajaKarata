package com.football.ticketsale.jobs;

import com.football.ticketsale.entity.TicketEntity;
import com.football.ticketsale.repository.SeatReservationRepository;
import com.football.ticketsale.repository.TicketRepository;
import com.football.ticketsale.service.CheckoutService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReservationCleanupJob {

    private final TicketRepository ticketRepository;
    private final SeatReservationRepository seatReservationRepository;

    public ReservationCleanupJob(TicketRepository ticketRepository,
                                 SeatReservationRepository seatReservationRepository) {
        this.ticketRepository = ticketRepository;
        this.seatReservationRepository = seatReservationRepository;
    }

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void expireOldReservations() {
        LocalDateTime now = LocalDateTime.now();

        List<TicketEntity> expired = ticketRepository
                .findByStatusAndReservedUntilBefore(CheckoutService.STATUS_RESERVED, now);

        for (TicketEntity t : expired) {
            if (t.getInvoiceEntity() != null) continue;

            t.setStatus("EXPIRED");
            ticketRepository.save(t);

            seatReservationRepository.deleteByTicket(t);
        }
    }
}
