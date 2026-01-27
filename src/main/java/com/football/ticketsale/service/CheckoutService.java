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
    public static final String STATUS_REFUNDED = "REFUNDED";

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

        LocalDateTime now = LocalDateTime.now();

        List<StadiumSectionEntity> sections = sectionRepository.findByStadiumOrderByStandNameAscSectionCodeAsc(stadium);
        Map<UUID, Long> avail = new HashMap<>();

        for (StadiumSectionEntity s : sections) {
            int start = s.getSeatStart();
            int end = s.getSeatEnd();

            // ✅ count seats taken by PAID + active RESERVED
            long taken = seatReservationRepository.countTakenInRange(match, start, end, now);

            long total = (long) end - start + 1;
            avail.put(s.getSectionUid(), Math.max(0, total - taken));
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

        LocalDateTime now = LocalDateTime.now();
        long active = ticketRepository.countActiveReservations(user, now);
        int limit = 12;

        if (active + req.getQuantity() > limit) {
            throw new IllegalStateException("You already have active reservations. Please pay or cancel them first.");
        }

        MatchEntity match = matchRepository.findById(req.getMatchId())
                .orElseThrow(() -> new EntityNotFoundException("Match not found"));

        // Only one active reservation per match (per user)
        List<TicketEntity> existingForMatch = ticketRepository.findReservedTicketsForUserAndMatch(
                user.getUserUid(),
                req.getMatchId(),
                STATUS_RESERVED,
                now
        );
        if (!existingForMatch.isEmpty()) {
            return buildResumeModel(username, req.getMatchId());
        }

        if (match.getStadium() == null) {
            throw new IllegalStateException("Match has no stadium configured");
        }

        StadiumSectionEntity section = sectionRepository
                .findByStadiumAndSectionCode(match.getStadium(), req.getSectionCode())
                .orElseThrow(() -> new EntityNotFoundException("Section not found for this stadium"));

        int start = section.getSeatStart();
        int end = section.getSeatEnd();

        // ✅ Treat PAID seats as taken too
        List<Integer> taken = seatReservationRepository.findTakenSeatNumbersInRange(match, start, end, now);
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
        LocalDateTime reservedUntil = LocalDateTime.now().plusMinutes(10);

        try {
            for (Integer seatNumber : selected) {

                // Extra safety: never allow re-assigning an existing (match,seat) row
                if (seatReservationRepository.existsByMatchAndSeatNumber(match, seatNumber)) {
                    throw new IllegalStateException("Some seats were just taken. Please try again.");
                }

                TicketEntity t = new TicketEntity();
                t.setUserEntity(user);
                t.setOwnerName(req.getOwnerName());
                t.setPin(req.getPin());
                t.setSectionCode(section.getSectionCode());
                t.setStatus(STATUS_RESERVED);
                t.setReservedUntil(reservedUntil);

                t = ticketRepository.save(t);

                SeatReservationEntity sr = new SeatReservationEntity();
                sr.setMatch(match);
                sr.setSeatNumber(seatNumber);
                sr.setTicket(t);
                seatReservationRepository.save(sr);

                reservedTickets.add(
                        new ReservedTicketDto(t.getTicketUid(), seatNumber, section.getSectionCode())
                );
            }
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Some seats were just taken. Please try again.");
        }

        return new ReserveSectionResponseDto(
                match.getMatchUid(),
                section.getSectionCode(),
                req.getQuantity(),
                total,
                reservedTickets,
                reservedUntil
        );
    }

    @Transactional
    public UUID pay(String username, PayRequestDto req) {
        if (req.getTicketIds() == null || req.getTicketIds().isEmpty()) {
            throw new IllegalArgumentException("No tickets to pay");
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<TicketEntity> tickets = ticketRepository.findAllByIdForUpdate(req.getTicketIds());
        if (tickets.size() != req.getTicketIds().size()) {
            throw new IllegalArgumentException("Some tickets not found");
        }

        LocalDateTime now = LocalDateTime.now();

        List<TicketEntity> expired = new ArrayList<>();
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

            if (t.getReservedUntil() == null || t.getReservedUntil().isBefore(now)) {
                expired.add(t);
            }
        }

        if (!expired.isEmpty()) {
            for (TicketEntity t : expired) {
                t.setStatus("EXPIRED");
                ticketRepository.save(t);
                seatReservationRepository.deleteByTicket(t);
            }
            throw new IllegalStateException("Reservation expired. Please reserve again.");
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

        LocalDateTime paidAt = LocalDateTime.now();
        for (TicketEntity t : tickets) {
            t.setInvoiceEntity(invoice);
            t.setStatus(STATUS_PAID);
            t.setPaidAt(paidAt);
            ticketRepository.save(t);
        }

        return invoice.getInvoiceUid();
    }

    @Transactional
    public void refundTicket(String username, UUID ticketId) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        TicketEntity t = ticketRepository.findAllByIdForUpdate(List.of(ticketId))
                .stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        if (!t.getUserEntity().getUserUid().equals(user.getUserUid())) {
            throw new SecurityException("You can only refund your own tickets");
        }
        if (!STATUS_PAID.equals(t.getStatus()) || t.getInvoiceEntity() == null) {
            throw new IllegalStateException("Only paid tickets can be refunded");
        }

        SeatReservationEntity sr = seatReservationRepository.findByTicket(t)
                .orElseThrow(() -> new IllegalStateException("Ticket has no seat reservation"));

        LocalDateTime matchTime = sr.getMatch().getMatchDatetime();
        if (matchTime.isBefore(LocalDateTime.now().plusDays(3))) {
            throw new IllegalStateException("Refunds are not allowed within 3 days of the match");
        }

        t.setStatus(STATUS_REFUNDED);
        t.setRefundedAt(LocalDateTime.now());
        ticketRepository.save(t);

        seatReservationRepository.delete(sr);

        InvoiceEntity inv = t.getInvoiceEntity();
        List<TicketEntity> invoiceTickets = ticketRepository.findByInvoiceEntity(inv);

        boolean allRefunded = invoiceTickets.stream().allMatch(x -> STATUS_REFUNDED.equals(x.getStatus()));
        if (allRefunded) {
            inv.setPaymentStatus("REFUNDED");
            invoiceRepository.save(inv);
        }
    }

    @Transactional
    public void cancelReservedForMatch(String username, UUID matchId) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<TicketEntity> reserved = ticketRepository
                .findByUserEntityAndStatusAndInvoiceEntityIsNullAndMatchId(user, STATUS_RESERVED, matchId);

        for (TicketEntity t : reserved) {
            seatReservationRepository.deleteByTicket(t);

            t.setStatus(TicketEntity.STATUS_CANCELLED);
            t.setReservedUntil(null);
            ticketRepository.save(t);
        }
    }

    @Transactional
    public ReserveSectionResponseDto buildResumeModel(String username, UUID matchId) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        MatchEntity match = matchRepository.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("Match not found"));

        List<TicketEntity> reservedTickets = ticketRepository.findReservedTicketsForUserAndMatch(
                user.getUserUid(),
                matchId,
                STATUS_RESERVED,
                LocalDateTime.now()
        );

        if (reservedTickets.isEmpty()) return null;

        LocalDateTime newestUntil = reservedTickets.stream()
                .map(TicketEntity::getReservedUntil)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        if (newestUntil != null) {
            List<TicketEntity> keep = reservedTickets.stream()
                    .filter(t -> newestUntil.equals(t.getReservedUntil()))
                    .toList();

            List<TicketEntity> cancel = reservedTickets.stream()
                    .filter(t -> !newestUntil.equals(t.getReservedUntil()))
                    .toList();

            for (TicketEntity t : cancel) {
                seatReservationRepository.deleteByTicket(t);
                t.setStatus(TicketEntity.STATUS_CANCELLED);
                t.setReservedUntil(null);
                ticketRepository.save(t);
            }

            reservedTickets = keep;
        }

        LocalDateTime reservedUntil = reservedTickets.stream()
                .map(TicketEntity::getReservedUntil)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        String sectionCode = reservedTickets.stream()
                .map(TicketEntity::getSectionCode)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .findFirst()
                .orElse(null);

        BigDecimal pricePerTicket = match.getBaseTicketPriceUsd();
        BigDecimal total = pricePerTicket.multiply(BigDecimal.valueOf(reservedTickets.size()));

        List<ReservedTicketDto> dtos = new ArrayList<>(reservedTickets.size());

        for (TicketEntity t : reservedTickets) {
            SeatReservationEntity sr = seatReservationRepository.findByTicket(t)
                    .orElseThrow(() -> new IllegalStateException("Ticket has no seat reservation"));

            String dtoSection = (t.getSectionCode() != null && !t.getSectionCode().isBlank())
                    ? t.getSectionCode()
                    : (sectionCode != null ? sectionCode : "");

            dtos.add(new ReservedTicketDto(
                    t.getTicketUid(),
                    sr.getSeatNumber(),
                    dtoSection
            ));
        }

        if (sectionCode == null) {
            sectionCode = dtos.stream()
                    .map(ReservedTicketDto::getSectionCode)
                    .filter(s -> s != null && !s.isBlank())
                    .findFirst()
                    .orElse("");
        }

        return new ReserveSectionResponseDto(
                match.getMatchUid(),
                sectionCode,
                reservedTickets.size(),
                total,
                dtos,
                reservedUntil
        );
    }
}
