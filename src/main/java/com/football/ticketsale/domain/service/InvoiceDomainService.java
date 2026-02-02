package com.football.ticketsale.domain.service;

import com.football.ticketsale.entity.InvoiceEntity;
import com.football.ticketsale.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class InvoiceDomainService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceDomainService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public Optional<InvoiceEntity> findById(UUID invoiceId) {
        return invoiceRepository.findById(invoiceId);
    }

    public List<InvoiceEntity> findAll() {
        return invoiceRepository.findAll();
    }

    public long count() {
        return invoiceRepository.count();
    }

    @Transactional
    public InvoiceEntity save(InvoiceEntity invoice) {
        return invoiceRepository.save(invoice);
    }

    @Transactional
    public void delete(InvoiceEntity invoice) {
        invoiceRepository.delete(invoice);
    }
}
