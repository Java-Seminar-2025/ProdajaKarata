package com.football.ticketsale.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "country")
public class CountryEntity {

    @Id
    @GeneratedValue
    @Column(name = "country_uid", nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID countryUid;

    @Column(name = "country_name", length = 20, nullable = false)
    private String countryName;

    @Column(name = "vat", nullable = false)
    private Double vat;

    protected CountryEntity() {} // JPA-safe

    public CountryEntity(String countryName, Double vat) {
        this.countryName = countryName;
        this.vat = vat;
    }


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