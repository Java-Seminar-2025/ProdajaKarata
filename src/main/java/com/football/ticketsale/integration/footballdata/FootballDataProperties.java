package com.football.ticketsale.integration.footballdata;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "footballdata")
public class FootballDataProperties {
    private String baseUrl;
    private String apiToken;
    private List<String> competitions;
    private int daysAhead = 30;
}
