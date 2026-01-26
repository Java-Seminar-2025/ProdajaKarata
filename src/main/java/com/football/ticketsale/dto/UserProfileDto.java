package com.football.ticketsale.dto;

import lombok.Data;

@Data
public class UserProfileDto {
    private String fullName;
    private String email;
    private String newPassword;
    private String confirmPassword;
}
