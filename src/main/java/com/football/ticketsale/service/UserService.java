package com.football.ticketsale.service;

import com.football.ticketsale.dto.UserProfileDto;
import com.football.ticketsale.dto.UserRegistrationDto;
import com.football.ticketsale.entity.UserEntity;

import java.util.List;

public interface UserService {
    void saveUser(UserRegistrationDto userDto);
    UserEntity findUserByEmail(String email);
    UserEntity findUserByUsername(String username);
    List<UserRegistrationDto> findAllUsers();

    void updateUser(String currentUsername, UserProfileDto userProfileDto) throws Exception;
    void deleteUser(String username);
}