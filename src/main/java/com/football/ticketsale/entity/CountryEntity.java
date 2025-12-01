package com.football.ticketsale.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "Country")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "Country_UID", updatable = false, nullable = false)
    private UUID countryUid;

    @Column(name = "Country_Name", length = 20, nullable = false)
    private String countryName;

    @Column(name = "VAT", nullable = false)
    private Double vat;
}