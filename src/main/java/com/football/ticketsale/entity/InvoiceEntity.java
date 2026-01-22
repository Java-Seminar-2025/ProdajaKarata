package com.football.ticketsale.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "[Invoice]")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "Invoice_UID", updatable = false, nullable = false)
    private UUID invoiceUid;

    @Column(name = "Paypal_Payment_ID", length = 40, nullable = false)
    private String paypalPaymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "User_UID",
            foreignKey = @ForeignKey(name = "FK_Invoice_User"))
    private UserEntity userEntity;

    @Column(name = "Payment_Status", length = 20, nullable = false)
    private String paymentStatus;

    @Column(name = "Currency", length = 3, nullable = false)
    private String currency;

    @Column(name = "Amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "Purchase_Qty", nullable = false)
    private Integer purchaseQty;

    @Column(name = "Creation_Timestamp", updatable = false)
    private LocalDateTime creationTimestamp;

    @PrePersist
    protected void onCreate() {
        if (creationTimestamp == null) {
            creationTimestamp = LocalDateTime.now();
        }
    }
}