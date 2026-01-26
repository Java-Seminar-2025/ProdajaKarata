package com.football.ticketsale.service;

import com.football.ticketsale.dto.UserProfileDto;
import com.football.ticketsale.dto.UserRegistrationDto;
import com.football.ticketsale.entity.UserEntity;
import com.football.ticketsale.entity.CountryEntity;
import com.football.ticketsale.repository.UserRepository;
import com.football.ticketsale.repository.CountryRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           CountryRepository countryRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void saveUser(UserRegistrationDto userRegistrationDto) {
        UserEntity user = new UserEntity();
        user.setUsername(userRegistrationDto.getUsername());
        user.setEmail(userRegistrationDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(userRegistrationDto.getPassword()));
        user.setAuthorizationLevel("user");
        user.setFullName(userRegistrationDto.getFullName());
        user.setPin(userRegistrationDto.getPin());

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(String currentUsername, UserProfileDto dto) throws Exception {
        UserEntity user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new Exception("Korisnik nije pronađen"));

        if (dto.getFullName() != null && !dto.getFullName().trim().isEmpty()) {
            user.setFullName(dto.getFullName());
        }

        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            user.setEmail(dto.getEmail());
        }

        if (dto.getNewPassword() != null && !dto.getNewPassword().trim().isEmpty()) {
            if (dto.getNewPassword().length() < 8) {
                throw new Exception("Lozinka mora imati najmanje 8 znakova!");
            }
            if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
                throw new Exception("Lozinke se ne podudaraju!");
            }
            user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        }

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(String username) {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            userRepository.delete(user);
        }
    }

    @Override
    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public UserEntity findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public List<UserRegistrationDto> findAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    private UserRegistrationDto mapToUserDto(UserEntity user) {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setId(user.getUserUid());
        userRegistrationDto.setUsername(user.getUsername());
        userRegistrationDto.setEmail(user.getEmail());
        return userRegistrationDto;
    }
}