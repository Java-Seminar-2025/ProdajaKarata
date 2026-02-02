package com.football.ticketsale.dto.checkout;

import com.football.ticketsale.entity.MatchEntity;
import com.football.ticketsale.entity.StadiumSectionEntity;
import com.football.ticketsale.entity.TicketTierEntity;
import com.football.ticketsale.entity.UserEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CheckoutPageDto(
        UserEntity user,
        MatchEntity match,
        ReserveSectionResponseDto reserved,
        ReserveSectionRequestDto reserveRequest,
        PayRequestDto payRequest,
        List<TicketTierEntity> tiers,
        Map<String, List<StadiumSectionEntity>> sectionsByStand,
        Map<UUID, Long> availability
) {}
