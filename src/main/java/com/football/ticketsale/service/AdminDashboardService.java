package com.football.ticketsale.service;

import com.football.ticketsale.domain.service.*;
import com.football.ticketsale.dto.admin.AdminDashboardDto;
import com.football.ticketsale.dto.admin.AdminInvoicesDto;
import com.football.ticketsale.dto.admin.AdminUserTicketsDto;
import com.football.ticketsale.dto.admin.CreateMatchForm;
import com.football.ticketsale.entity.InvoiceEntity;
import com.football.ticketsale.entity.TicketEntity;
import com.football.ticketsale.entity.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AdminDashboardService {

    private final MatchDomainService matchDomainService;
    private final UserDomainService userDomainService;
    private final FootballClubDomainService clubDomainService;
    private final StadiumDomainService stadiumDomainService;
    private final TicketDomainService ticketDomainService;
    private final InvoiceDomainService invoiceDomainService;

    private final MatchSyncService matchSyncService;
    private final MatchService matchService;
    private final AdminTicketService adminTicketService;

    public AdminDashboardService(
            MatchDomainService matchDomainService,
            UserDomainService userDomainService,
            FootballClubDomainService clubDomainService,
            StadiumDomainService stadiumDomainService,
            TicketDomainService ticketDomainService,
            InvoiceDomainService invoiceDomainService,
            MatchSyncService matchSyncService,
            MatchService matchService,
            AdminTicketService adminTicketService
    ) {
        this.matchDomainService = matchDomainService;
        this.userDomainService = userDomainService;
        this.clubDomainService = clubDomainService;
        this.stadiumDomainService = stadiumDomainService;
        this.ticketDomainService = ticketDomainService;
        this.invoiceDomainService = invoiceDomainService;
        this.matchSyncService = matchSyncService;
        this.matchService = matchService;
        this.adminTicketService = adminTicketService;
    }

    @Transactional(readOnly = true)
    public AdminDashboardDto loadDashboard() {
        return new AdminDashboardDto(
                matchDomainService.count(),
                userDomainService.count(),
                userDomainService.countByAuthorizationLevel("admin"),
                userDomainService.findAll(),
                clubDomainService.findAll(),
                stadiumDomainService.findAll(),
                new CreateMatchForm()
        );
    }

    @Transactional(readOnly = true)
    public AdminUserTicketsDto loadUserTickets(UUID userId) {
        UserEntity user = userDomainService.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Invalid user Id: " + userId));

        List<TicketEntity> tickets = ticketDomainService.findByUser(user);
        return new AdminUserTicketsDto(user, tickets);
    }

    @Transactional
    public UUID refundOrCancelTicket(UUID ticketId) {
        TicketEntity ticket = ticketDomainService.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket id"));
        UUID userId = ticket.getUserEntity().getUserUid();

        adminTicketService.refundOrCancelTicket(ticketId);
        return userId;
    }

    @Transactional(readOnly = true)
    public AdminInvoicesDto loadInvoices() {
        List<InvoiceEntity> invoices = invoiceDomainService.findAll();

        BigDecimal totalRevenue = invoices.stream()
                .map(InvoiceEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AdminInvoicesDto(
                invoices,
                invoiceDomainService.count(),
                totalRevenue
        );
    }

    @Transactional
    public void syncMatches() {
        matchSyncService.syncUpcoming();
    }

    @Transactional
    public void promoteUser(UUID userId) {
        UserEntity user = userDomainService.findById(userId).orElse(null);
        if (user != null && !"admin".equalsIgnoreCase(user.getAuthorizationLevel())) {
            user.setAuthorizationLevel("admin");
            userDomainService.save(user);
        }
    }

    @Transactional
    public void createMatch(CreateMatchForm form) {
        matchService.createMatch(
                form.getHomeClubId(),
                form.getAwayClubId(),
                form.getStadiumId(),
                form.getMatchDateTime(),
                form.getPrice(),
                form.getCompetitionCode(),
                form.getStatus()
        );
    }
}
