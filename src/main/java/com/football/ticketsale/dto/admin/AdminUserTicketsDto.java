package com.football.ticketsale.dto.admin;

import com.football.ticketsale.entity.TicketEntity;
import com.football.ticketsale.entity.UserEntity;

import java.util.List;

public record AdminUserTicketsDto(
        UserEntity user,
        List<TicketEntity> tickets
) {}
