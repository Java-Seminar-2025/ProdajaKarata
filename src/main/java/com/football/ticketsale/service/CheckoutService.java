package com.football.ticketsale.service;

import com.football.ticketsale.domain.service.*;
import com.football.ticketsale.dto.checkout.*;
import com.football.ticketsale.entity.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CheckoutService {

    public static final String STATUS_RESERVED = "RESERVED";
    public static final String STATUS_PAID = "PAID";
    public static final String STATUS_REFUNDED = "REFUNDED";

    private final MatchDomainService matchDomainService;
    private final StadiumSectionDomainService stadiumSectionDomainService;
    private final SeatReservationDomainService seatReservationDomainService;
    private final TicketDomainService ticketDomainService;
    private final InvoiceDomainService invoiceDomainService;
    private final UserDomainService userDomainService;
    private final TicketTierDomainService ticketTierDomainService;

    public CheckoutService(
            MatchDomainService matchDomainService,
            StadiumSectionDomainService stadiumSectionDomainService,
            SeatReservationDomainService seatReservationDomainService,
            TicketDomainService ticketDomainService,
            InvoiceDomainService invoiceDomainService,
            UserDomainService userDomainService,
            TicketTierDomainService ticketTierDomainService
    ) {
        this.matchDomainService = matchDomainService;
        this.stadiumSectionDomainService = stadiumSectionDomainService;
        this.seatReservationDomainService = seatReservationDomainService;
        this.ticketDomainService = ticketDomainService;
        this.invoiceDomainService = invoiceDomainService;
        this.userDomainService = userDomainService;
        this.ticketTierDomainService = ticketTierDomainService;
    }

    @Transactional(readOnly = true)
    public CheckoutPageDto buildCheckoutPage(String username, UUID matchId) {
        UserEntity user = userDomainService.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        MatchEntity match = matchDomainService.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("Match not found"));

        ReserveSectionResponseDto resumed = buildResumeModel(username, matchId);

        ReserveSectionRequestDto reserveReq = new ReserveSectionRequestDto();
        reserveReq.setMatchId(matchId);

        PayRequestDto payReq = new PayRequestDto();

        Map<String, List<StadiumSectionEntity>> sectionsByStand = null;
        Map<UUID, Long> availability = null;

        if (resumed == null) {
            List<StadiumSectionEntity> sections = getSectionsForMatch(matchId);
            availability = getAvailabilityBySection(matchId);

            sectionsByStand = sections.stream()
                    .collect(java.util.stream.Collectors.groupingBy(
                            s -> s.getStandName() == null ? "Sections" : s.getStandName(),
                            LinkedHashMap::new,
                            java.util.stream.Collectors.toList()
                    ));
        }

        List<TicketTierEntity> tiers = ticketTierDomainService.findAll();

        return new CheckoutPageDto(
                user,
                match,
                resumed,
                reserveReq,
                payReq,
                tiers,
                sectionsByStand,
                availability
        );
    }

    @Transactional(readOnly = true)
    public List<StadiumSectionEntity> getSectionsForMatch(UUID matchId) {
        MatchEntity match = matchDomainService.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("Match not found"));

        StadiumEntity stadium = match.getStadium();
        if (stadium == null) return List.of();

        return stadiumSectionDomainService.findByStadiumOrdered(stadium);
    }

    @Transactional(readOnly = true)
    public Map<UUID, Long> getAvailabilityBySection(UUID matchId) {
        MatchEntity match = matchDomainService.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("Match not found"));

        StadiumEntity stadium = match.getStadium();
        if (stadium == null) return Map.of();

        LocalDateTime now = LocalDateTime.now();

        List<StadiumSectionEntity> sections = stadiumSectionDomainService.findByStadiumOrdered(stadium);
        Map<UUID, Long> avail = new HashMap<>();

        for (StadiumSectionEntity s : sections) {
            int start = s.getSeatStart();
            int end = s.getSeatEnd();

            long taken = seatReservationDomainService.countTakenInRange(match, start, end, now);
            long total = (long) end - start + 1;

            avail.put(s.getSectionUid(), Math.max(0, total - taken));
        }
        return avail;
    }

    @Transactional(readOnly = true)
    public void resumeReservation(String username, UUID matchId, String sectionCodeIgnored, LocalDateTime reservedUntilIgnored) {
        UserEntity user = userDomainService.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Active reservation: status RESERVED, no invoice, and reservedUntil > now
        LocalDateTime now = LocalDateTime.now();
        List<TicketEntity> active = ticketDomainService.findReservedTicketsForUserAndMatch(
                user.getUserUid(),
                matchId,
                STATUS_RESERVED,
                now
        );

        if (active.isEmpty()) {
            throw new IllegalStateException("No active reservation to resume");
        }
    }

    private void deleteSeatReservationAndDetach(TicketEntity ticket) {
        seatReservationDomainService.deleteByTicket(ticket);
        ticket.setSeatReservation(null);
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
        if (req.getTierUid() == null) {
            throw new IllegalArgumentException("Ticket tier is required");
        }

        UserEntity user = userDomainService.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        LocalDateTime now = LocalDateTime.now();
        long active = ticketDomainService.countActiveReservations(user, now);
        int limit = 12;

        if (active + req.getQuantity() > limit) {
            throw new IllegalStateException("You already have active reservations. Please pay or cancel them first.");
        }

        MatchEntity match = matchDomainService.findById(req.getMatchId())
                .orElseThrow(() -> new EntityNotFoundException("Match not found"));

        List<TicketEntity> existingForMatch = ticketDomainService.findReservedTicketsForUserAndMatch(
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

        StadiumSectionEntity section = stadiumSectionDomainService
                .findByStadiumAndSectionCode(match.getStadium(), req.getSectionCode())
                .orElseThrow(() -> new EntityNotFoundException("Section not found for this stadium"));

        TicketTierEntity tier = ticketTierDomainService.findById(req.getTierUid())
                .orElseThrow(() -> new EntityNotFoundException("Ticket tier not found"));

        int start = section.getSeatStart();
        int end = section.getSeatEnd();

        List<Integer> taken = seatReservationDomainService.findTakenSeatNumbersInRange(match, start, end, now);
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

        BigDecimal base = match.getBaseTicketPriceUsd();
        BigDecimal modifier = BigDecimal.valueOf(
                tier.getPriceModifier() != null ? tier.getPriceModifier() : 1.0
        );
        BigDecimal pricePerTicket = base.multiply(modifier).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = pricePerTicket
                .multiply(BigDecimal.valueOf(req.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP);

        LocalDateTime reservedUntil = LocalDateTime.now().plusMinutes(10);

        List<ReservedTicketDto> reservedTickets = new ArrayList<>();

        try {
            for (Integer seatNumber : selected) {

                if (seatReservationDomainService.existsActiveSeatReservation(match, seatNumber, now)) {
                    throw new IllegalStateException("Some seats were just taken. Please try again.");
                }

                TicketEntity t = new TicketEntity();
                t.setUserEntity(user);
                t.setOwnerName(req.getOwnerName());
                t.setPin(req.getPin());
                t.setSectionCode(section.getSectionCode());
                t.setTierEntity(tier);
                t.setStatus(STATUS_RESERVED);
                t.setReservedUntil(reservedUntil);

                t = ticketDomainService.save(t);

                SeatReservationEntity sr = new SeatReservationEntity();
                sr.setMatch(match);
                sr.setSeatNumber(seatNumber);
                sr.setTicket(t);
                seatReservationDomainService.save(sr);

                reservedTickets.add(new ReservedTicketDto(t.getTicketUid(), seatNumber, section.getSectionCode()));
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

        UserEntity user = userDomainService.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<TicketEntity> tickets = ticketDomainService.findAllByIdForUpdate(req.getTicketIds());
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
                detachAndDeleteSeatReservation(t);
                ticketDomainService.save(t);
            }
            throw new IllegalStateException("Reservation expired. Please reserve again.");
        }

        BigDecimal total = BigDecimal.ZERO;

        for (TicketEntity t : tickets) {
            SeatReservationEntity sr = seatReservationDomainService.findByTicket(t)
                    .orElseThrow(() -> new IllegalStateException("Ticket has no seat reservation"));

            BigDecimal base = sr.getMatch().getBaseTicketPriceUsd();
            double mod = 1.0;

            if (t.getTierEntity() != null && t.getTierEntity().getPriceModifier() != null) {
                mod = t.getTierEntity().getPriceModifier();
            }

            BigDecimal price = base.multiply(BigDecimal.valueOf(mod)).setScale(2, RoundingMode.HALF_UP);
            total = total.add(price);
        }

        total = total.setScale(2, RoundingMode.HALF_UP);

        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setUser(user);
        invoice.setCurrency("USD");
        invoice.setAmount(total);
        invoice.setPurchaseQty(tickets.size());
        invoice.setPaymentStatus("PAID");
        invoice.setPaypalPaymentId("MOCK-" + UUID.randomUUID());
        invoice = invoiceDomainService.save(invoice);

        LocalDateTime paidAt = LocalDateTime.now();
        for (TicketEntity t : tickets) {
            t.setInvoiceEntity(invoice);
            t.setStatus(STATUS_PAID);
            t.setPaidAt(paidAt);
            ticketDomainService.save(t);
        }

        return invoice.getInvoiceUid();
    }

    @Transactional
    public void refundTicket(String username, UUID ticketId) {
        UserEntity user = userDomainService.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        TicketEntity t = ticketDomainService.findAllByIdForUpdate(List.of(ticketId))
                .stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        if (!t.getUserEntity().getUserUid().equals(user.getUserUid())) {
            throw new SecurityException("You can only refund your own tickets");
        }
        if (!STATUS_PAID.equals(t.getStatus()) || t.getInvoiceEntity() == null) {
            throw new IllegalStateException("Only paid tickets can be refunded");
        }

        SeatReservationEntity sr = seatReservationDomainService.findByTicket(t)
                .orElseThrow(() -> new IllegalStateException("Ticket has no seat reservation"));

        LocalDateTime matchTime = sr.getMatch().getMatchDatetime();
        if (matchTime.isBefore(LocalDateTime.now().plusDays(3))) {
            throw new IllegalStateException("Refunds are not allowed within 3 days of the match");
        }

        seatReservationDomainService.delete(sr);
        t.setSeatReservation(null);

        t.setStatus(STATUS_REFUNDED);
        t.setRefundedAt(LocalDateTime.now());
        ticketDomainService.save(t);

        InvoiceEntity inv = t.getInvoiceEntity();
        List<TicketEntity> invoiceTickets = ticketDomainService.findByInvoice(inv);

        boolean allRefunded = invoiceTickets.stream().allMatch(x -> STATUS_REFUNDED.equals(x.getStatus()));
        if (allRefunded) {
            inv.setPaymentStatus("REFUNDED");
            invoiceDomainService.save(inv);
        }
    }

    @Transactional
    public void cancelReservedForMatch(String username, UUID matchId) {
        UserEntity user = userDomainService.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        LocalDateTime now = LocalDateTime.now();

        List<TicketEntity> reserved = ticketDomainService.findByUserStatusNoInvoiceMatch(
                user,
                STATUS_RESERVED,
                matchId,
                now
        );

        for (TicketEntity t : reserved) {
            detachAndDeleteSeatReservation(t);

            t.setStatus(TicketEntity.STATUS_CANCELLED);
            t.setReservedUntil(null);
            ticketDomainService.save(t);
        }
    }

public ReserveSectionResponseDto buildResumeModel(String username, UUID matchId) {
        UserEntity user = userDomainService.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        MatchEntity match = matchDomainService.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("Match not found"));

        List<TicketEntity> reservedTickets = ticketDomainService.findReservedTicketsForUserAndMatch(
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
                detachAndDeleteSeatReservation(t);
                t.setStatus(TicketEntity.STATUS_CANCELLED);
                t.setReservedUntil(null);
                ticketDomainService.save(t);
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

        BigDecimal total = BigDecimal.ZERO;
        List<ReservedTicketDto> dtos = new ArrayList<>(reservedTickets.size());

        for (TicketEntity t : reservedTickets) {
            SeatReservationEntity sr = seatReservationDomainService.findByTicket(t)
                    .orElseThrow(() -> new IllegalStateException("Ticket has no seat reservation"));

            String dtoSection = (t.getSectionCode() != null && !t.getSectionCode().isBlank())
                    ? t.getSectionCode()
                    : (sectionCode != null ? sectionCode : "");

            dtos.add(new ReservedTicketDto(
                    t.getTicketUid(),
                    sr.getSeatNumber(),
                    dtoSection
            ));

            BigDecimal base = match.getBaseTicketPriceUsd();
            double mod = 1.0;
            if (t.getTierEntity() != null && t.getTierEntity().getPriceModifier() != null) {
                mod = t.getTierEntity().getPriceModifier();
            }
            total = total.add(base.multiply(BigDecimal.valueOf(mod)));
        }

        total = total.setScale(2, RoundingMode.HALF_UP);

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

    private void detachAndDeleteSeatReservation(TicketEntity ticket) {
        if (ticket == null) return;
        seatReservationDomainService.deleteByTicket(ticket);
        ticket.setSeatReservation(null);
    }
}
