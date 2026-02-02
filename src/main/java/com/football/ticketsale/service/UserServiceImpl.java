package com.football.ticketsale.service;

import com.football.ticketsale.domain.service.UserDomainService;
import com.football.ticketsale.dto.UserProfileDto;
import com.football.ticketsale.dto.UserRegistrationDto;
import com.football.ticketsale.entity.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserDomainService userDomainService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserDomainService userDomainService, PasswordEncoder passwordEncoder) {
        this.userDomainService = userDomainService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void saveUser(UserRegistrationDto userDto) {
        UserEntity user = new UserEntity();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setFullName(userDto.getFullName());
        user.setPin(userDto.getPin());
        user.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));
        user.setAuthorizationLevel("user");

        userDomainService.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity findUserByEmail(String email) {
        if (email == null || email.isBlank()) return null;
        return userDomainService.findByEmail(email).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity findUserByUsername(String username) {
        if (username == null || username.isBlank()) return null;
        return userDomainService.findByUsername(username).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRegistrationDto> findAllUsers() {
        return userDomainService.findAll().stream()
                .map(this::mapToRegistrationDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateUser(String currentUsername, UserProfileDto userProfileDto) throws Exception {
        if (currentUsername == null || currentUsername.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }

        UserEntity user = userDomainService.findByUsername(currentUsername)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String newEmail = userProfileDto.getEmail();
        if (newEmail != null && !newEmail.isBlank() && !newEmail.equalsIgnoreCase(user.getEmail())) {
            if (userDomainService.existsByEmail(newEmail)) {
                throw new IllegalStateException("Email is already in use");
            }
            user.setEmail(newEmail);
        }

        if (userProfileDto.getFullName() != null && !userProfileDto.getFullName().isBlank()) {
            user.setFullName(userProfileDto.getFullName());
        }

        String newPassword = userProfileDto.getNewPassword();
        if (newPassword != null && !newPassword.isBlank()) {
            String confirm = userProfileDto.getConfirmPassword();
            if (confirm == null || !newPassword.equals(confirm)) {
                throw new IllegalArgumentException("Passwords do not match");
            }
            user.setPasswordHash(passwordEncoder.encode(newPassword));
        }

        userDomainService.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(String username) {
        if (username == null || username.isBlank()) return;
        userDomainService.findByUsername(username).ifPresent(userDomainService::delete);
    }

    private UserRegistrationDto mapToRegistrationDto(UserEntity user) {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setId(user.getUserUid());
        dto.setFullName(user.getFullName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPin(user.getPin());
        return dto;
    }
}
