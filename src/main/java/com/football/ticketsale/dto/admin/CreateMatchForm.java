package com.football.ticketsale.dto.admin;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


public class CreateMatchForm {


    private UUID homeClubId;
    private UUID awayClubId;
    private UUID stadiumId;
    private LocalDateTime matchDateTime;
    private BigDecimal price;

    private String competitionCode;
    private String status;


    public UUID getHomeClubId() {
        return homeClubId;
    }


    public void setHomeClubId(UUID homeClubId) {
        this.homeClubId = homeClubId;
    }


    public UUID getAwayClubId() {
        return awayClubId;
    }


    public void setAwayClubId(UUID awayClubId) {
        this.awayClubId = awayClubId;
    }


    public UUID getStadiumId() {
        return stadiumId;
    }


    public void setStadiumId(UUID stadiumId) {
        this.stadiumId = stadiumId;
    }


    public LocalDateTime getMatchDateTime() {
        return matchDateTime;
    }


    public void setMatchDateTime(LocalDateTime matchDateTime) {
        this.matchDateTime = matchDateTime;
    }


    public BigDecimal getPrice() {
        return price;
    }


    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setCompetitionCode(String competitionCode) {
        this.competitionCode = competitionCode;
    }

    public String getCompetitionCode() {
        return competitionCode;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}