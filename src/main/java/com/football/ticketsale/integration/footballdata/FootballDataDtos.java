package com.football.ticketsale.integration.footballdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

public class FootballDataDtos {

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MatchesResponse {
        private List<ApiMatch> matches;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApiMatch {
        private Long id;
        private String utcDate;
        private String status;
        private Competition competition;
        private TeamRef homeTeam;
        private TeamRef awayTeam;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Competition {
        private String code;
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamRef {
        private Long id;
        private String name;
        private String shortName;
    }
}
