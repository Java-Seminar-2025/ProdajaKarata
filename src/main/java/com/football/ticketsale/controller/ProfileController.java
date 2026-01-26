package com.football.ticketsale.controller;

import ch.qos.logback.core.util.StringUtil;
import com.football.ticketsale.dto.UserProfileDto;
import com.football.ticketsale.entity.UserEntity;
import com.football.ticketsale.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/settings")
    public String showSettings(Model model, Principal principal) {
        String username = principal.getName();
        UserEntity userEntity = userService.findUserByUsername(username);

        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setFullName(userEntity.getFullName());
        userProfileDto.setEmail(userEntity.getEmail());

        model.addAttribute("user", userEntity);
        model.addAttribute("userProfileDto", userProfileDto);

        model.addAttribute("role", StringUtil.capitalizeFirstLetter(userEntity.getAuthorizationLevel()));

        return "settings";
    }

    @PostMapping("/settings/update")
    public String updateProfile(@ModelAttribute("userProfileDto") UserProfileDto dto,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(principal.getName(), dto);
            redirectAttributes.addFlashAttribute("successMessage", "Profil uspješno ažuriran!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/settings";
    }

    @PostMapping("/settings/delete")
    public String deleteAccount(Principal principal) {
        userService.deleteUser(principal.getName());
        return "redirect:/logout";
    }
}