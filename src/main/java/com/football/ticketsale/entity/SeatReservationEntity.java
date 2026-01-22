package com.football.ticketsale.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "[Seat_Reservation]")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(SeatReservationId.class)
public class SeatReservationEntity {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Match_UID", nullable = false,
            foreignKey = @ForeignKey(name = "FK_SeatReservation_Match"))
    private MatchEntity match;

    @Id
    @Column(name = "Seat_Number", nullable = false)
    private Integer seatNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Ticket_UID", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "FK_SeatReservation_Ticket"))
    private TicketEntity ticket;
}