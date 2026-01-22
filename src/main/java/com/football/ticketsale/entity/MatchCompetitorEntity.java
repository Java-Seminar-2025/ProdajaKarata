package com.football.ticketsale.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "[Match_Competitors]")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(MatchCompetitorId.class)
public class MatchCompetitorEntity {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Match_ID", nullable = false,
            foreignKey = @ForeignKey(name = "FK_MatchCompetitors_Match"))
    private MatchEntity matchEntity;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Club_ID", nullable = false,
            foreignKey = @ForeignKey(name = "FK_MatchCompetitors_FootballClub"))
    private FootballClubEntity clubEntity;
}

