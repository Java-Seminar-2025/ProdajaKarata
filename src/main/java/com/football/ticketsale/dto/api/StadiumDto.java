package com.football.ticketsale.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StadiumDto {
    private UUID stadiumUid;
    private String stadiumName;
    private Integer numberOfSeats;
}
