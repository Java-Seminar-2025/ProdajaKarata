package com.football.ticketsale.dto.checkout;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ReservedTicketDto {
    private UUID ticketId;
    private Integer seatNumber;
    private String sectionCode;
}
