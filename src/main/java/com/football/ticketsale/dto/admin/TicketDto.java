package com.football.ticketsale.dto.admin;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.UUID;

@Data
public class TicketDto {
    private UUID ticketUid;
    private String ownerName;
    private String pin;
    private String status;
    private String matchName;

    public TicketDto(UUID ticketUid, String ownerName, @Pattern(regexp = "\\d{11}", message = "PIN must be exactly 11 digits") String pin, String status) {
    }
}
