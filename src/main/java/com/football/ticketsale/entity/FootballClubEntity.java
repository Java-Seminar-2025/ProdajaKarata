package com.football.ticketsale.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "football_club")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FootballClubEntity {

    @Id
    @GeneratedValue
    @Column(name = "club_uid", nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID clubUid;

    @Column(name = "club_name", length = 100, nullable = false)
    private String clubName;

    @Column(name = "source", length = 30)
    private String source;

    @Column(name = "source_team_id", length = 40)
    private String sourceTeamId;

    @Column(name = "total_players", nullable = false)
    private Integer totalPlayers;
}
