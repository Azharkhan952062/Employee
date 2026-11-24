package com.ems.employeemanagement.controller;

import com.ems.employeemanagement.dto.UserProfileDto;
import com.ems.employeemanagement.dto.UserRegistrationDto;
import com.ems.employeemanagement.exception.DuplicateResourceException;
import com.ems.employeemanagement.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    private boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
    }

    @GetMapping("/login")
    public String showLoginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (isAuthenticated()) {
            return "redirect:/dashboard";
        }

        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email or password.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been logged out successfully.");
        }

        model.addAttribute("pageTitle", "Sign In");
        return "login";
    }

    @GetMapping({"/signup", "/register"})
    public String showSignupPage(Model model) {
        if (isAuthenticated()) {
            return "redirect:/dashboard";
        }

        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserRegistrationDto());
        }
        model.addAttribute("pageTitle", "Create Account");
        return "signup";
    }

    @PostMapping({"/signup", "/register"})
    public String registerUser(
            @Valid @ModelAttribute("user") UserRegistrationDto registrationDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (registrationDto.getPassword() != null && registrationDto.getConfirmPassword() != null
                && !registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.user", "Passwords do not match");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Create Account");
            return "signup";
        }

        try {
            userService.registerUser(registrationDto);
            redirectAttributes.addFlashAttribute("successMessage", "Account created successfully! You can now log in.");
            return "redirect:/login";
        } catch (DuplicateResourceException e) {
            bindingResult.rejectValue("email", "error.user", "This email is already registered. Please log in.");
            model.addAttribute("duplicateEmail", true);
            model.addAttribute("pageTitle", "Create Account");
            return "signup";
        } catch (Exception e) {
            log.error("Error during registration", e);
            model.addAttribute("errorMessage", "Registration failed: " + e.getMessage());
            model.addAttribute("pageTitle", "Create Account");
            return "signup";
        }
    }

    @GetMapping("/profile")
    public String showProfile(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        UserProfileDto profile = userService.getUserProfile(principal.getName());
        model.addAttribute("profile", profile);
        model.addAttribute("pageTitle", "My Profile");
        model.addAttribute("activeNav", "profile");
        return "profile";
    }
}
