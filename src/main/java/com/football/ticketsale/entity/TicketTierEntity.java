package com.football.ticketsale.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.rmi.server.UID;

@Entity
@Table(name = "ticket_tier")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketTierEntity {
    @Id
    @Column(name = "tier_uid", columnDefinition = "BINARY(16)")
    private UID id;

    @Column(name = "tier_name", unique = true, nullable = false)
    private String tierName;

    @Column(name = "price_modifier", nullable = false)
    private Double priceModifier;
}
