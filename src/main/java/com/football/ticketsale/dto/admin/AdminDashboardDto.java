package com.football.ticketsale.dto.admin;

import com.football.ticketsale.entity.FootballClubEntity;
import com.football.ticketsale.entity.StadiumEntity;
import com.football.ticketsale.entity.UserEntity;

import java.util.List;

public record AdminDashboardDto(
        long matchCount,
        long userCount,
        long adminCount,
        List<UserEntity> users,
        List<FootballClubEntity> clubs,
        List<StadiumEntity> stadiums,
        CreateMatchForm createMatchForm
) {}
