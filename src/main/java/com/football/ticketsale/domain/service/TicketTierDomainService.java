package com.football.ticketsale.domain.service;

import com.football.ticketsale.entity.TicketTierEntity;
import com.football.ticketsale.repository.TicketTierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TicketTierDomainService {

    private final TicketTierRepository ticketTierRepository;

    public TicketTierDomainService(TicketTierRepository ticketTierRepository) {
        this.ticketTierRepository = ticketTierRepository;
    }

    public List<TicketTierEntity> findAll() {
        return ticketTierRepository.findAll();
    }

    public Optional<TicketTierEntity> findById(UUID id) {
        return ticketTierRepository.findById(id);
    }
}
