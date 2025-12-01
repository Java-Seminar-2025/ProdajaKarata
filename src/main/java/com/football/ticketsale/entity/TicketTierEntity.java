package com.football.ticketsale.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "Ticket_Tier")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketTierEntity {
    @Id
    @Column(name = "Tier_Name", length = 20)
    private String tierName;

    @Column(name = "Price_Modifier", nullable = false)
    private Double priceModifier;
}