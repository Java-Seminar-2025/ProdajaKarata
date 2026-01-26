package com.football.ticketsale.integration.footballdata;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static com.football.ticketsale.integration.footballdata.FootballDataDtos.*;

@Component
public class FootballDataClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final FootballDataProperties props;

    public FootballDataClient(FootballDataProperties props) {
        this.props = props;
    }

    public MatchesResponse getUpcomingMatches(String competitionCode, String dateFrom, String dateTo) {
        String url = UriComponentsBuilder
                .fromHttpUrl(props.getBaseUrl() + "/competitions/" + competitionCode + "/matches")
                .queryParam("dateFrom", dateFrom) // yyyy-MM-dd
                .queryParam("dateTo", dateTo)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", props.getApiToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<MatchesResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, MatchesResponse.class
        );

        return response.getBody();
    }
}
