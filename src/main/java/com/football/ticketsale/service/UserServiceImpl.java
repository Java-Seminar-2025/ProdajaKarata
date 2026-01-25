package com.football.ticketsale.service;

import com.football.ticketsale.dto.UserDto;
import com.football.ticketsale.entity.UserEntity;
import com.football.ticketsale.entity.CountryEntity;
import com.football.ticketsale.repository.UserRepository;
import com.football.ticketsale.repository.CountryRepository;
import jakarta.transaction.Transactional;
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
    public void saveUser(UserDto userDto) {
        UserEntity user = new UserEntity();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));
        user.setAuthorizationLevel("user");
        user.setFullName(userDto.getFullName());
        user.setPin(userDto.getPin());

        userRepository.save(user);
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
    public List<UserDto> findAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    private UserDto mapToUserDto(UserEntity user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getUserUid());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        return userDto;
    }
}