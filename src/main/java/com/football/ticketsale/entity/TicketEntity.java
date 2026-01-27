package com.football.ticketsale.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ticket")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ticket_uid", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID ticketUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uid", nullable = false, columnDefinition = "BINARY(16)",
            foreignKey = @ForeignKey(name = "fk_ticket_user"))
    private UserEntity userEntity;

    @Column(name = "owner_name", length = 20, nullable = false)
    private String ownerName;

    @Pattern(regexp = "\\d{11}", message = "PIN must be exactly 11 digits")
    @Column(name = "pin", length = 11, nullable = false)
    private String pin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_uid", referencedColumnName = "tier_uid")
    private TicketTierEntity tierEntity;

    @Column(name = "status", length = 20)
    private String status = "pending";

    @Column(name = "creation_datetime", updatable = false)
    private LocalDateTime creationDatetime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_uid", columnDefinition = "BINARY(16)",
            foreignKey = @ForeignKey(name = "fk_ticket_invoice"))
    private InvoiceEntity invoiceEntity;

    @PrePersist
    protected void onCreate() {
        if (creationDatetime == null) {
            creationDatetime = LocalDateTime.now();
        }
        if (status == null) {
            status = "pending";
        }
    }

    @OneToOne(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private SeatReservationEntity seatReservation;
}
