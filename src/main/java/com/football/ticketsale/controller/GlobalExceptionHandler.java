package com.football.ticketsale.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ IllegalStateException.class, IllegalArgumentException.class })
    public String handleBadRequest(RuntimeException ex,
                                   HttpServletRequest req,
                                   RedirectAttributes ra) {

        ra.addFlashAttribute("error", ex.getMessage());

        String referer = req.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            return "redirect:" + referer;
        }

        return "redirect:/home";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrity(DataIntegrityViolationException ex,
                                      HttpServletRequest req,
                                      RedirectAttributes ra) {
        ra.addFlashAttribute("error", "That seat was just taken. Please try again.");
        String referer = req.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/home");
    }
}
