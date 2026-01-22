package com.football.ticketsale.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "[Stadium]")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StadiumEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "Stadium_UID", updatable = false, nullable = false)
    private UUID stadiumUid;

    @Column(name = "Stadium_Name", length = 20, nullable = false)
    private String stadiumName;

    @Column(name = "Number_Of_Seats", nullable = false)
    private Integer numberOfSeats;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "City",
                foreignKey = @ForeignKey(name = "FK_Stadium_City"))
    private CityEntity cityEntity;
}