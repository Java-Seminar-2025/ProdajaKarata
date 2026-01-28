package com.football.ticketsale.service;

import com.football.ticketsale.entity.InvoiceEntity;
import com.football.ticketsale.entity.SeatReservationEntity;
import com.football.ticketsale.entity.TicketEntity;
import com.football.ticketsale.repository.InvoiceRepository;
import com.football.ticketsale.repository.SeatReservationRepository;
import com.football.ticketsale.repository.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AdminTicketService {

    private final TicketRepository ticketRepository;
    private final SeatReservationRepository seatReservationRepository;
    private final InvoiceRepository invoiceRepository;

    public AdminTicketService(TicketRepository ticketRepository,
                              SeatReservationRepository seatReservationRepository,
                              InvoiceRepository invoiceRepository) {
        this.ticketRepository = ticketRepository;
        this.seatReservationRepository = seatReservationRepository;
        this.invoiceRepository = invoiceRepository;
    }


    @Transactional
    public void refundOrCancelTicket(UUID ticketId) {

        TicketEntity t = ticketRepository.findAllByIdForUpdate(List.of(ticketId))
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        String status = t.getStatus();

        SeatReservationEntity sr = seatReservationRepository.findByTicket(t).orElse(null);

        if (TicketEntity.STATUS_RESERVED.equals(status)) {

            t.setSeatReservation(null);

            t.setStatus(TicketEntity.STATUS_CANCELLED);
            t.setReservedUntil(null);

            ticketRepository.save(t);

            return;
        }


        if (TicketEntity.STATUS_PAID.equals(status)) {
            if (sr == null) {
                throw new IllegalStateException("Paid ticket has no seat reservation (cannot determine match time)");
            }

            LocalDateTime matchTime = sr.getMatch().getMatchDatetime();
            if (matchTime == null) {
                throw new IllegalStateException("Match time is missing");
            }

            if (!matchTime.isAfter(LocalDateTime.now().plusDays(3))) {
                throw new IllegalStateException("Refunds are not allowed within 3 days of the match");
            }

            t.setStatus(TicketEntity.STATUS_REFUNDED);
            t.setRefundedAt(LocalDateTime.now());
            ticketRepository.save(t);

            seatReservationRepository.delete(sr);

            InvoiceEntity inv = t.getInvoiceEntity();
            if (inv != null) {
                List<TicketEntity> invoiceTickets = ticketRepository.findByInvoiceEntity(inv);
                boolean allRefunded = invoiceTickets.stream()
                        .allMatch(x -> TicketEntity.STATUS_REFUNDED.equals(x.getStatus()));

                if (allRefunded) {
                    inv.setPaymentStatus("REFUNDED");
                    invoiceRepository.save(inv);
                }
            }

            return;
        }

        throw new IllegalStateException("Ticket cannot be cancelled/refunded from status: " + status);
    }
}
