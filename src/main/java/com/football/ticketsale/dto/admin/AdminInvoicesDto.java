package com.football.ticketsale.dto.admin;

import com.football.ticketsale.entity.InvoiceEntity;

import java.math.BigDecimal;
import java.util.List;

public record AdminInvoicesDto(
        List<InvoiceEntity> invoices,
        long invoiceCount,
        BigDecimal totalRevenue
) {}
