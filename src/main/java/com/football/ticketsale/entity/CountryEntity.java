package com.football.ticketsale.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "Country")
public class CountryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Country_UID", updatable = false, nullable = false)
    private UUID countryUid;

    @Column(name = "Country_Name", length = 20, nullable = false)
    private String countryName;

    @Column(name = "VAT", nullable = false)
    private Double vat;

    // Constructors
    public CountryEntity() {}

    public CountryEntity(UUID countryUid, String countryName, Double vat) {
        this.countryUid = countryUid;
        this.countryName = countryName;
        this.vat = vat;
    }

    // Getters and Setters
    public UUID getCountryUid() {
        return countryUid;
    }

    public void setCountryUid(UUID countryUid) {
        this.countryUid = countryUid;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Double getVat() {
        return vat;
    }

    public void setVat(Double vat) {
        this.vat = vat;
    }
}