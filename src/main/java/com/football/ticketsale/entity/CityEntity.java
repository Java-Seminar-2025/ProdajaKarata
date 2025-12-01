package com.football.ticketsale.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "City")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityEntity {
    @Id
    @Column(name = "Zip_Code", length = 20)
    private String zipCode;

    @Column(name = "City_Name", length = 20, nullable = false)
    private String cityName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Country_UID", nullable = false,
            foreignKey = @ForeignKey(name = "FK_City_Country"))
    private CountryEntity countryEntity;
}