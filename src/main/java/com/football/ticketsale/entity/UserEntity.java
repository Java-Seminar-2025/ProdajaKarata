package com.football.ticketsale.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "[User]")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "User_UID", updatable = false, nullable = false)
    private UUID userUid;

    @Column(name = "Username", length = 20, nullable = false, unique = true)
    private String username;

    @Column(name = "E_Mail", length = 30, nullable = false, unique = true)
    private String email;

    @Column(name = "Password_Hash", length = 60, nullable = false)
    private String passwordHash;

    @Column(name = "Full_Name", length = 40, nullable = false)
    private String fullName;

    @Pattern(regexp = "\\d{11}", message = "PIN must be exactly 11 digits")
    @Column(name = "PIN", length = 11, nullable = false)
    private String pin;

    @Column(name = "Authorization_Level", length = 10)
    private String authorizationLevel = "user";

    @Column(name = "Creation_Timestamp", updatable = false)
    private LocalDateTime creationTimestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Country_UID",
            foreignKey = @ForeignKey(name = "FK_User_Country"))
    private CountryEntity country;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TicketEntity> ticketEntities;

    @PrePersist
    protected void onCreate() {
        if (creationTimestamp == null) {
            creationTimestamp = LocalDateTime.now();
        }
        if (authorizationLevel == null) {
            authorizationLevel = "user";
        }
    }
}