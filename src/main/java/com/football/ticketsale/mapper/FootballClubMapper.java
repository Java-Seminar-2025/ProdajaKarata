package com.football.ticketsale.mapper;

import com.football.ticketsale.dto.api.FootballClubDto;
import com.football.ticketsale.entity.FootballClubEntity;

public final class FootballClubMapper {

    private FootballClubMapper() {}

    public static FootballClubDto toDto(FootballClubEntity e) {
        if (e == null) return null;
        return new FootballClubDto(
                e.getClubUid(),
                e.getClubName(),
                null,
                e.getSource(),
                e.getTotalPlayers()
        );
    }
}
