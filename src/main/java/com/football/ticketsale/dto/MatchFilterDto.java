package com.football.ticketsale.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
public class MatchFilterDto {
    private String q;
    private List<String> competitions;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    private LocalDate dateFrom;
    private LocalDate dateTo;

    private LocalTime timeFrom;
    private LocalTime timeTo;

    private String club;
    private String city;
    private UUID countryId;
    private UUID cityId;
    private String competition;
}
