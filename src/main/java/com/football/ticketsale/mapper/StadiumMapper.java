package com.football.ticketsale.mapper;

import com.football.ticketsale.dto.api.StadiumDto;
import com.football.ticketsale.entity.StadiumEntity;

public final class StadiumMapper {

    private StadiumMapper() {}

    public static StadiumDto toDto(StadiumEntity e) {
        if (e == null) return null;
        return new StadiumDto(
                e.getStadiumUid(),
                e.getStadiumName(),
                e.getNumberOfSeats()
        );
    }
}
