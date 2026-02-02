package com.football.ticketsale.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FootballClubDto {
    private UUID clubUid;
    private String clubName;
    private String country;
    private String source;
    private Integer totalPlayers;
}
