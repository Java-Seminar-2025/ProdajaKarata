package com.football.ticketsale.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "seat_reservation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(SeatReservationId.class)
public class SeatReservationEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "match_uid", columnDefinition = "BINARY(16)",
            foreignKey = @ForeignKey(name = "fk_seat_reservation_match")
    )
    private MatchEntity match;

    @Id
    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "ticket_uid",
            unique = true, columnDefinition = "BINARY(16)",
            foreignKey = @ForeignKey(name = "fk_seat_reservation_ticket")
    )
    private TicketEntity ticket;
}
