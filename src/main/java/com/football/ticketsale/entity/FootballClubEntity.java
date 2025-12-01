package com.football.ticketsale.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "Football_Club")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FootballClubEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "Club_UID", updatable = false, nullable = false)
    private UUID clubUid;

    @Column(name = "Club_Name", length = 20, nullable = false)
    private String clubName;

    @Column(name = "Total_Players", nullable = false)
    private Integer totalPlayers;
}