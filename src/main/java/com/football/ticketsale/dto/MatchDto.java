package com.football.ticketsale.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MatchDto {
    private String Status;
    private String competitionCode;
    private UUID homeClubId;
    private UUID awayClubId;
    private UUID stadiumId;
    private LocalDateTime matchDateTime;
    private BigDecimal price;

}
