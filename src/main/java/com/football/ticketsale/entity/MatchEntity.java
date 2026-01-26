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
@Table(name = "`match`")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchEntity {

    @Id
    @GeneratedValue
    @Column(name = "match_uid", nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID matchUid;

    @Column(name = "match_datetime")
    private LocalDateTime matchDatetime;

    @Column(name = "base_ticket_price_usd", precision = 10, scale = 2, nullable = false)
    private BigDecimal baseTicketPriceUsd;

    @Column(name = "source", length = 30)
    private String source;

    @Column(name = "source_match_id", length = 40)
    private String sourceMatchId;

    @Column(name = "competition_code", length = 20)
    private String competitionCode;

    @Column(name = "status", length = 20)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "home_club_uid", columnDefinition = "BINARY(16)",
            foreignKey = @ForeignKey(name = "fk_match_home_club")
    )
    private FootballClubEntity homeTeam;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "away_club_uid", columnDefinition = "BINARY(16)",
            foreignKey = @ForeignKey(name = "fk_match_away_club")
    )
    private FootballClubEntity awayTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "stadium_uid",
            foreignKey = @ForeignKey(name = "fk_match_stadium")
    )
    private StadiumEntity stadium;

    @OneToMany(
            mappedBy = "match",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )

    private Set<SeatReservationEntity> seatReservations;
}
