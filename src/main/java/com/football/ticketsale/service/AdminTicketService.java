package com.football.ticketsale.service;

import com.football.ticketsale.domain.service.InvoiceDomainService;
import com.football.ticketsale.domain.service.SeatReservationDomainService;
import com.football.ticketsale.domain.service.TicketDomainService;
import com.football.ticketsale.entity.InvoiceEntity;
import com.football.ticketsale.entity.SeatReservationEntity;
import com.football.ticketsale.entity.TicketEntity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AdminTicketService {

    private final TicketDomainService ticketDomainService;
    private final SeatReservationDomainService seatReservationDomainService;
    private final InvoiceDomainService invoiceDomainService;

    public AdminTicketService(
            TicketDomainService ticketDomainService,
            SeatReservationDomainService seatReservationDomainService,
            InvoiceDomainService invoiceDomainService
    ) {
        this.ticketDomainService = ticketDomainService;
        this.seatReservationDomainService = seatReservationDomainService;
        this.invoiceDomainService = invoiceDomainService;
    }

    @Transactional
    public void refundOrCancelTicket(UUID ticketUid) {

        TicketEntity t = ticketDomainService.findAllByIdForUpdate(List.of(ticketUid))
                .stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        String status = t.getStatus();

        SeatReservationEntity sr = seatReservationDomainService.findByTicket(t).orElse(null);

        // Cancel reserved tickets (admin action)
        if (TicketEntity.STATUS_RESERVED.equals(status)) {

            if (sr != null) {
                seatReservationDomainService.delete(sr);
            }

            t.setStatus(TicketEntity.STATUS_CANCELLED);
            t.setReservedUntil(null);
            ticketDomainService.save(t);

            return;
        }

        // Refund paid tickets (within policy window)
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
            ticketDomainService.save(t);

            seatReservationDomainService.delete(sr);

            InvoiceEntity inv = t.getInvoiceEntity();
            if (inv != null) {
                List<TicketEntity> invoiceTickets = ticketDomainService.findByInvoice(inv);
                boolean allRefunded = invoiceTickets.stream()
                        .allMatch(x -> TicketEntity.STATUS_REFUNDED.equals(x.getStatus()));

                if (allRefunded) {
                    inv.setPaymentStatus("REFUNDED");
                    invoiceDomainService.save(inv);
                }
            }

            return;
        }

        throw new IllegalStateException("Ticket cannot be cancelled/refunded from status: " + status);
    }
}
