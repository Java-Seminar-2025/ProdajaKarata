package com.football.ticketsale.dto.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyTicketsPageDto {
    private String username;
    private String role;
    private List<ActiveReservationDto> activeReservations;
    private List<TicketViewDto> tickets;
}
