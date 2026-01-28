package com.football.ticketsale.dto.checkout;

import lombok.Data;

import java.util.UUID;

@Data
public class ReserveSectionRequestDto {
    private UUID matchId;
    private String sectionCode;
    private Integer quantity;
    private String ownerName;
    private String pin;

    private UUID tierUid;
}
