package com.football.ticketsale.service;

import com.football.ticketsale.dto.UserDto;
import com.football.ticketsale.entity.UserEntity;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);
    UserEntity findUserByEmail(String email);
    UserEntity findUserByUsername(String username);
    List<UserDto> findAllUsers();
}