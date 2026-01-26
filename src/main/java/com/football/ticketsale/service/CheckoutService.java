package com.football.ticketsale.service;

import com.football.ticketsale.dto.checkout.*;
import com.football.ticketsale.entity.*;
import com.football.ticketsale.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CheckoutService {

    public static final String STATUS_RESERVED = "RESERVED";
    public static final String STATUS_PAID = "PAID";

    private final MatchRepository matchRepository;
    private final StadiumSectionRepository sectionRepository;
    private final SeatReservationRepository seatReservationRepository;
    private final TicketRepository ticketRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;

    public CheckoutService(
            MatchRepository matchRepository,
            StadiumSectionRepository sectionRepository,
            SeatReservationRepository seatReservationRepository,
            TicketRepository ticketRepository,
            InvoiceRepository invoiceRepository,
            UserRepository userRepository
    ) {
        this.matchRepository = matchRepository;
        this.sectionRepository = sectionRepository;
        this.seatReservationRepository = seatReservationRepository;
        this.ticketRepository = ticketRepository;
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<StadiumSectionEntity> getSectionsForMatch(UUID matchId) {
        MatchEntity match = matchRepository.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("Match not found"));
        StadiumEntity stadium = match.getStadium();
        if (stadium == null) return List.of();
        return sectionRepository.findByStadiumOrderByStandNameAscSectionCodeAsc(stadium);
    }

    @Transactional(readOnly = true)
    public Map<UUID, Long> getAvailabilityBySection(UUID matchId) {
        MatchEntity match = matchRepository.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("Match not found"));
        StadiumEntity stadium = match.getStadium();
        if (stadium == null) return Map.of();

        List<StadiumSectionEntity> sections = sectionRepository.findByStadiumOrderByStandNameAscSectionCodeAsc(stadium);
        Map<UUID, Long> avail = new HashMap<>();
        for (StadiumSectionEntity s : sections) {
            int start = s.getSeatStart();
            int end = s.getSeatEnd();
            long reserved = seatReservationRepository.countReservedInRange(match, start, end);
            long total = (long) end - start + 1;
            avail.put(s.getSectionUid(), Math.max(0, total - reserved));
        }
        return avail;
    }

    @Transactional
    public ReserveSectionResponseDto reserveSection(String username, ReserveSectionRequestDto req) {
        if (req.getQuantity() == null || req.getQuantity() < 1 || req.getQuantity() > 6) {
            throw new IllegalArgumentException("Quantity must be between 1 and 6");
        }
        if (req.getSectionCode() == null || req.getSectionCode().isBlank()) {
            throw new IllegalArgumentException("Section is required");
        }
        if (req.getOwnerName() == null || req.getOwnerName().isBlank()) {
            throw new IllegalArgumentException("Owner name is required");
        }
        if (req.getPin() == null || !req.getPin().matches("\\d{11}")) {
            throw new IllegalArgumentException("PIN must be exactly 11 digits");
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        MatchEntity match = matchRepository.findById(req.getMatchId())
                .orElseThrow(() -> new EntityNotFoundException("Match not found"));

        if (match.getStadium() == null) {
            throw new IllegalStateException("Match has no stadium configured");
        }

        StadiumSectionEntity section = sectionRepository
                .findByStadiumAndSectionCode(match.getStadium(), req.getSectionCode())
                .orElseThrow(() -> new EntityNotFoundException("Section not found for this stadium"));

        int start = section.getSeatStart();
        int end = section.getSeatEnd();


        List<Integer> taken = seatReservationRepository.findSeatNumbersInRange(match, start, end);
        Set<Integer> takenSet = new HashSet<>(taken);

        List<Integer> selected = new ArrayList<>();
        for (int seat = start; seat <= end && selected.size() < req.getQuantity(); seat++) {
            if (!takenSet.contains(seat)) {
                selected.add(seat);
            }
        }

        if (selected.size() < req.getQuantity()) {
            throw new IllegalStateException("Not enough seats available in this section");
        }


        List<ReservedTicketDto> reservedTickets = new ArrayList<>();
        BigDecimal price = match.getBaseTicketPriceUsd();
        BigDecimal total = price.multiply(BigDecimal.valueOf(req.getQuantity()));

        try {
            for (Integer seatNumber : selected) {
                TicketEntity t = new TicketEntity();
                t.setUserEntity(user);
                t.setOwnerName(req.getOwnerName());
                t.setPin(req.getPin());
                t.setStatus(STATUS_RESERVED);

                t = ticketRepository.save(t);

                SeatReservationEntity sr = new SeatReservationEntity();
                sr.setMatch(match);
                sr.setSeatNumber(seatNumber);
                sr.setTicket(t);
                seatReservationRepository.save(sr);

                reservedTickets.add(new ReservedTicketDto(t.getTicketUid(), seatNumber, section.getSectionCode()));
            }
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Some seats were just taken. Please try again.");
        }

        return new ReserveSectionResponseDto(match.getMatchUid(), section.getSectionCode(),
                req.getQuantity(), total, reservedTickets);
    }

    @Transactional
    public UUID pay(String username, PayRequestDto req) {
        if (req.getTicketIds() == null || req.getTicketIds().isEmpty()) {
            throw new IllegalArgumentException("No tickets to pay");
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<TicketEntity> tickets = ticketRepository.findAllById(req.getTicketIds());

        for (TicketEntity t : tickets) {
            if (t.getUserEntity() == null || !t.getUserEntity().getUserUid().equals(user.getUserUid())) {
                throw new SecurityException("You can only pay your own tickets");
            }
            if (!STATUS_RESERVED.equals(t.getStatus())) {
                throw new IllegalStateException("Ticket is not in RESERVED state");
            }
            if (t.getInvoiceEntity() != null) {
                throw new IllegalStateException("Ticket already paid");
            }
        }


        BigDecimal total = BigDecimal.ZERO;
        for (TicketEntity t : tickets) {
            SeatReservationEntity sr = seatReservationRepository.findByTicket(t)
                    .orElseThrow(() -> new IllegalStateException("Ticket has no seat reservation"));
            total = total.add(sr.getMatch().getBaseTicketPriceUsd());
        }

        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setUser(user);
        invoice.setCurrency("USD");
        invoice.setAmount(total);
        invoice.setPurchaseQty(tickets.size());
        invoice.setPaymentStatus("PAID");

        invoice.setPaypalPaymentId("MOCK-" + UUID.randomUUID());

        invoice = invoiceRepository.save(invoice);

        for (TicketEntity t : tickets) {
            t.setInvoiceEntity(invoice);
            t.setStatus(STATUS_PAID);
            ticketRepository.save(t);
        }

        return invoice.getInvoiceUid();
    }
}
