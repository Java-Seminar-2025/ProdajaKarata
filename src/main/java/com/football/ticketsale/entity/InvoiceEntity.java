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
@Table(name = "invoice")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceEntity {

    @Id
    @GeneratedValue
    @Column(name = "invoice_uid", nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID invoiceUid;

    @Column(name = "paypal_payment_id", length = 100, nullable = false)
    private String paypalPaymentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_uid", columnDefinition = "BINARY(16)",
            foreignKey = @ForeignKey(name = "fk_invoice_user")
    )
    private UserEntity user;

    @Column(name = "payment_status", length = 20, nullable = false)
    private String paymentStatus;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "purchase_qty", nullable = false)
    private Integer purchaseQty;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
