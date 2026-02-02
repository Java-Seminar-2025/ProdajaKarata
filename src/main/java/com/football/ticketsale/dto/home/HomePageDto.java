package com.football.ticketsale.dto.home;

import com.football.ticketsale.dto.MatchFilterDto;
import com.football.ticketsale.entity.CountryEntity;
import com.football.ticketsale.entity.MatchEntity;
import com.football.ticketsale.entity.UserEntity;

import java.util.List;

public record HomePageDto(
        UserEntity user,
        String role,
        List<MatchEntity> matches,
        MatchFilterDto filter,
        List<String> competitionOptions,
        List<CountryEntity> countries
) {}
