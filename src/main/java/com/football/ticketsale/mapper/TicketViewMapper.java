package com.football.ticketsale.mapper;

import com.football.ticketsale.dto.view.TicketViewDto;
import com.football.ticketsale.entity.TicketEntity;

public final class TicketViewMapper {

    private TicketViewMapper() {}

    public static TicketViewDto toDto(
            TicketEntity t,
            Integer seatNumber,
            String sectionCode,
            String invoiceUid,
            boolean refundable
    ) {
        if (t == null) return null;
        String seatInfo = (seatNumber != null) ? String.valueOf(seatNumber) : "N/A";
        String section = (sectionCode != null && !sectionCode.isBlank()) ? sectionCode : "N/A";
        String invoice = (invoiceUid != null && !invoiceUid.isBlank()) ? invoiceUid : "N/A";

        return new TicketViewDto(
                t.getTicketUid(),
                t.getStatus(),
                t.getOwnerName(),
                seatInfo,
                section,
                invoice,
                refundable
        );
    }
}
