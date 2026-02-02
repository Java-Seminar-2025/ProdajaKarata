package com.football.ticketsale.repository.spec;

import com.football.ticketsale.dto.MatchFilterDto;
import com.football.ticketsale.entity.MatchEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

public class MatchSpecifications {
    public static Specification<MatchEntity> filterMatches(MatchFilterDto f) {
        return byFilter(f);
    }

    public static Specification<MatchEntity> byFilter(MatchFilterDto f) {
        return (root, query, cb) -> {
            query.distinct(true);

            var predicates = cb.conjunction();


            predicates = cb.and(predicates,
                    cb.greaterThanOrEqualTo(root.get("matchDatetime"), LocalDateTime.now()));


            if (f.getQ() != null && !f.getQ().isBlank()) {
                String needle = "%" + f.getQ().trim().toLowerCase(Locale.ROOT) + "%";
                var homeName = cb.lower(root.get("homeTeam").get("clubName"));
                var awayName = cb.lower(root.get("awayTeam").get("clubName"));
                var comp = cb.lower(root.get("competitionCode"));
                predicates = cb.and(predicates,
                        cb.or(
                                cb.like(homeName, needle),
                                cb.like(awayName, needle),
                                cb.like(comp, needle)
                        ));
            }

            if (f.getCompetitions() != null && !f.getCompetitions().isEmpty()) {
                var ors = cb.disjunction();
                for (String c : f.getCompetitions()) {
                    if (c == null || c.isBlank()) continue;
                    String needle = c.trim().toLowerCase(Locale.ROOT) + "%";
                    ors = cb.or(ors, cb.like(cb.lower(root.get("competitionCode")), needle));
                }
                predicates = cb.and(predicates, ors);
            }

            if (f.getMinPrice() != null) {
                predicates = cb.and(predicates,
                        cb.greaterThanOrEqualTo(root.get("baseTicketPriceUsd"), f.getMinPrice()));
            }
            if (f.getMaxPrice() != null) {
                predicates = cb.and(predicates,
                        cb.lessThanOrEqualTo(root.get("baseTicketPriceUsd"), f.getMaxPrice()));
            }

            if (f.getDateFrom() != null) {
                LocalDateTime start = f.getDateFrom().atStartOfDay();
                predicates = cb.and(predicates,
                        cb.greaterThanOrEqualTo(root.get("matchDatetime"), start));
            }
            if (f.getDateTo() != null) {
                LocalDateTime end = f.getDateTo().atTime(LocalTime.MAX);
                predicates = cb.and(predicates,
                        cb.lessThanOrEqualTo(root.get("matchDatetime"), end));
            }

            if (f.getTimeFrom() != null || f.getTimeTo() != null) {
                String from = (f.getTimeFrom() != null ? f.getTimeFrom().toString() : "00:00");
                String to = (f.getTimeTo() != null ? f.getTimeTo().toString() : "23:59");

                String fromS = from.length() == 5 ? from + ":00" : from;
                String toS = to.length() == 5 ? to + ":00" : to;

                var timeExpr = cb.function("TIME", String.class, root.get("matchDatetime"));
                predicates = cb.and(predicates, cb.between(timeExpr, fromS, toS));
            }

            if (f.getClub() != null && !f.getClub().isBlank()) {
                String needle = "%" + f.getClub().trim().toLowerCase(Locale.ROOT) + "%";
                var homeName = cb.lower(root.get("homeTeam").get("clubName"));
                var awayName = cb.lower(root.get("awayTeam").get("clubName"));
                predicates = cb.and(predicates,
                        cb.or(cb.like(homeName, needle), cb.like(awayName, needle)));
            }

            Join<Object, Object> stadiumJoin = null;
            Join<Object, Object> cityJoin = null;

            if (f.getCityId() != null) {
                stadiumJoin = root.join("stadium", JoinType.LEFT);
                cityJoin = stadiumJoin.join("cityEntity", JoinType.LEFT);
                predicates = cb.and(predicates, cb.equal(cityJoin.get("id"), f.getCityId()));
            }

            if (f.getCountryId() != null) {
                if (stadiumJoin == null) stadiumJoin = root.join("stadium", JoinType.LEFT);
                if (cityJoin == null) cityJoin = stadiumJoin.join("cityEntity", JoinType.LEFT);

                var countryJoin = cityJoin.join("country", JoinType.LEFT);
                predicates = cb.and(predicates, cb.equal(countryJoin.get("countryUid"), f.getCountryId()));
            }

            if (f.getCity() != null && !f.getCity().isBlank()) {
                String needle = "%" + f.getCity().trim().toLowerCase(Locale.ROOT) + "%";
                if (stadiumJoin == null) stadiumJoin = root.join("stadium", JoinType.LEFT);
                if (cityJoin == null) cityJoin = stadiumJoin.join("cityEntity", JoinType.LEFT);

                predicates = cb.and(predicates,
                        cb.like(cb.lower(cityJoin.get("cityName")), needle));
            }

            return predicates;
        };
    }
}
