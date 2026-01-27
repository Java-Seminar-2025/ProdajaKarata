package com.football.ticketsale.validation;

import com.football.ticketsale.dto.UserRegistrationDto;
import com.football.ticketsale.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class RegistrationValidator implements Validator {

    private final UserService userService;

    public RegistrationValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRegistrationDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserRegistrationDto dto = (UserRegistrationDto) target;

        if (dto.getPassword() != null && dto.getRepeatPassword() != null
                && !dto.getPassword().equals(dto.getRepeatPassword())) {
            errors.rejectValue(
                    "repeatPassword",
                    "password.mismatch",
                    "Passwords do not match"
            );
        }

        if (dto.getEmail() != null
                && userService.findUserByEmail(dto.getEmail()) != null) {
            errors.rejectValue(
                    "email",
                    "email.exists",
                    "There is already an account registered with this email"
            );
        }

        if (dto.getUsername() != null
                && userService.findUserByUsername(dto.getUsername()) != null) {
            errors.rejectValue(
                    "username",
                    "username.exists",
                    "There is already an account registered with this username"
            );
        }
    }
}
