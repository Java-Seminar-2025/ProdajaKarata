package com.football.ticketsale.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "[Match]")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "Match_UID", updatable = false, nullable = false)
    private UUID matchUid;

    @Column(name = "Match_Datetime")
    private LocalDateTime matchDatetime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Stadium",
            foreignKey = @ForeignKey(name = "FK_Match_Stadium"))
    private StadiumEntity stadiumEntity;

    @Column(name = "Base_Ticket_Price_USD", precision = 10, scale = 2, nullable = false)
    private BigDecimal baseTicketPriceUsd;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SeatReservationEntity> seatReservationEntities;
}