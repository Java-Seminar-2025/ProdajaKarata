package com.football.ticketsale.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Ticket")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "Ticket_UID", updatable = false, nullable = false)
    private UUID ticketUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "User_UID", nullable = false,
            foreignKey = @ForeignKey(name = "FK_Ticket_User"))
    private UserEntity userEntity;

    @Column(name = "Owner_Name", length = 20, nullable = false)
    private String ownerName;

    @Pattern(regexp = "\\d{11}", message = "PIN must be exactly 11 digits")
    @Column(name = "PIN", length = 11, nullable = false)
    private String pin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Tier",
            foreignKey = @ForeignKey(name = "FK_Ticket_TicketTier"))
    private TicketTierEntity tierEntity;

    @Column(name = "Status", length = 20)
    private String status = "pending";

    @Column(name = "Creation_Datetime", updatable = false)
    private LocalDateTime creationDatetime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Invoice_ID",
            foreignKey = @ForeignKey(name = "FK_Ticket_Invoice"))
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
}