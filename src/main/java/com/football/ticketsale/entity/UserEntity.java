package com.football.ticketsale.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "`user`")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_uid", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID userUid;

    @Column(name = "username", length = 20, nullable = false, unique = true)
    private String username;

    @Column(name = "e_mail", length = 30, nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", length = 60, nullable = false)
    private String passwordHash;

    @Column(name = "full_name", length = 40, nullable = false)
    private String fullName;

    @Pattern(regexp = "\\d{11}", message = "PIN must be exactly 11 digits")
    @Column(name = "pin", length = 11, nullable = true)
    private String pin;

    @Column(name = "authorization_level", length = 10)
    private String authorizationLevel = "USER";

    @Column(name = "creation_timestamp", updatable = true)
    private LocalDateTime creationTimestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "country_uid", nullable = true, columnDefinition = "BINARY(16)",
            foreignKey = @ForeignKey(name = "fk_user_country")
    )
    private CountryEntity country;

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TicketEntity> ticketEntities;

    public UserEntity() {}

    public UserEntity(UUID userUid, String username, String email, String passwordHash,
                      String fullName, String pin, String authorizationLevel,
                      LocalDateTime creationTimestamp, CountryEntity country) {
        this.userUid = userUid;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.pin = pin;
        this.authorizationLevel = authorizationLevel;
        this.creationTimestamp = creationTimestamp;
        this.country = country;
    }

    public UUID getUserUid() {
        return userUid;
    }

    public void setUserUid(UUID userUid) {
        this.userUid = userUid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getAuthorizationLevel() {
        return authorizationLevel;
    }

    public void setAuthorizationLevel(String authorizationLevel) {
        this.authorizationLevel = authorizationLevel;
    }

    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(LocalDateTime creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public CountryEntity getCountry() {
        return country;
    }

    public void setCountry(CountryEntity country) {
        this.country = country;
    }

    public Set<TicketEntity> getTicketEntities() {
        return ticketEntities;
    }

    public void setTicketEntities(Set<TicketEntity> ticketEntities) {
        this.ticketEntities = ticketEntities;
    }

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
