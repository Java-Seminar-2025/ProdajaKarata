package com.football.ticketsale.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatReservationId implements Serializable {
    private UUID match;
    private Integer seatNumber;
}
