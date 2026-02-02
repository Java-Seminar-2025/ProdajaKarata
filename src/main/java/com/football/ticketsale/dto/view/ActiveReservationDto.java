package com.football.ticketsale.dto.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActiveReservationDto {
    private UUID matchId;
    private String sectionCode;
    private Integer quantity;
    private String ownerName;
    private LocalDateTime reservedUntil;
}
