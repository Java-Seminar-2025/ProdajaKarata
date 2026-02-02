package com.football.ticketsale.service;

import com.football.ticketsale.domain.service.CountryDomainService;
import com.football.ticketsale.domain.service.UserDomainService;
import com.football.ticketsale.dto.MatchFilterDto;
import com.football.ticketsale.dto.home.HomePageDto;
import com.football.ticketsale.entity.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HomePageService {

    private final UserDomainService userDomainService;
    private final CountryDomainService countryDomainService;
    private final MatchService matchService;

    public HomePageService(
            UserDomainService userDomainService,
            CountryDomainService countryDomainService,
            MatchService matchService
    ) {
        this.userDomainService = userDomainService;
        this.countryDomainService = countryDomainService;
        this.matchService = matchService;
    }

    @Transactional(readOnly = true)
    public HomePageDto loadHomePage(String username, Authentication authentication, MatchFilterDto filter) {
        UserEntity user = userDomainService.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String role = "USER";
        if (authentication != null && authentication.getAuthorities() != null) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
            if (isAdmin) role = "ADMIN";
        }

        return new HomePageDto(
                user,
                role,
                matchService.searchMatches(filter),
                filter,
                matchService.getCompetitionOptions(),
                countryDomainService.findAll()
        );
    }
}
