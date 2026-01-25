package com.football.ticketsale.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "city")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityEntity {

    @Id
    @Column(name = "city_uid", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "zip_code", unique = true, nullable = false)
    private String zipCode;

    @Column(name = "city_name", length = 20, nullable = false)
    private String cityName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "country_uid", columnDefinition = "BINARY(16)",
            foreignKey = @ForeignKey(name = "fk_city_country")
    )
    private CountryEntity country;
}
