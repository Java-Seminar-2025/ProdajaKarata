package com.football.ticketsale.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "stadium")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StadiumEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "stadium_uid", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID stadiumUid;

    @Column(name = "stadium_name", length = 20, nullable = false)
    private String stadiumName;

    @Column(name = "number_of_seats", nullable = false)
    private Integer numberOfSeats;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_uid", referencedColumnName = "city_uid")
    private CityEntity cityEntity;
}
