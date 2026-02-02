package com.football.ticketsale.dto.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketViewDto {
    private UUID ticketUid;
    private String status;
    private String ownerName;

    private String seatInfo;

    private String sectionCode;

    private String invoiceUid;

    private boolean refundable;
}
