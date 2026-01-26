package com.football.ticketsale.dto.checkout;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ReserveSectionResponseDto {
    private UUID matchId;
    private String sectionCode;
    private Integer quantity;
    private BigDecimal totalPrice;
    private List<ReservedTicketDto> tickets;
}
