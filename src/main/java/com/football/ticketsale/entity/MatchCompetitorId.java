package com.football.ticketsale.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchCompetitorId implements Serializable {
    private UUID match;
    private UUID club;
}
