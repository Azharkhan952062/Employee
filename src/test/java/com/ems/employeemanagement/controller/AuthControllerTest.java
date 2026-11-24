package com.ems.employeemanagement.controller;

import com.ems.employeemanagement.config.SecurityConfig;
import com.ems.employeemanagement.dto.UserProfileDto;
import com.ems.employeemanagement.dto.UserRegistrationDto;
import com.ems.employeemanagement.entity.User;
import com.ems.employeemanagement.exception.DuplicateResourceException;
import com.ems.employeemanagement.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithAnonymousUser
    @DisplayName("GET /login - Should render login view for unauthenticated user")
    void loginPage_RendersSuccessfully() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("pageTitle"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("GET /signup - Should render signup view for unauthenticated user")
    void signupPage_RendersSuccessfully() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeExists("user", "pageTitle"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("POST /signup - Should redirect to /login on valid user registration")
    void registerUser_Valid_RedirectsToLogin() throws Exception {
        User user = new User(1L, "Jane Doe", "jane.doe@company.com", "hash", "ROLE_ADMIN");
        when(userService.registerUser(any(UserRegistrationDto.class))).thenReturn(user);

        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .param("fullName", "Jane Doe")
                        .param("email", "jane.doe@company.com")
                        .param("password", "Secure123!")
                        .param("confirmPassword", "Secure123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("POST /signup - Should fail and show error when passwords do not match")
    void registerUser_PasswordMismatch_ReturnsSignupWithError() throws Exception {
        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .param("fullName", "Jane Doe")
                        .param("email", "jane.doe@company.com")
                        .param("password", "Secure123!")
                        .param("confirmPassword", "DifferentPassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("POST /signup - Should fail and show error when email is already registered")
    void registerUser_DuplicateEmail_ReturnsSignupWithError() throws Exception {
        when(userService.registerUser(any(UserRegistrationDto.class)))
                .thenThrow(new DuplicateResourceException("User", "email", "existing@company.com"));

        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .param("fullName", "Jane Doe")
                        .param("email", "existing@company.com")
                        .param("password", "Secure123!")
                        .param("confirmPassword", "Secure123!"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors());
    }

    @Test
    @WithMockUser(username = "admin@ems.com", roles = {"ADMIN"})
    @DisplayName("GET /profile - Should render profile view with user info for authenticated user")
    void profilePage_Authenticated_RendersProfile() throws Exception {
        UserProfileDto profile = new UserProfileDto();
        profile.setId(1L);
        profile.setFullName("HR Administrator");
        profile.setEmail("admin@ems.com");
        profile.setRole("ADMIN");
        profile.setCreatedAt(LocalDateTime.now());

        when(userService.getUserProfile("admin@ems.com")).thenReturn(profile);

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("profile", "pageTitle", "activeNav"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("GET /profile - Should redirect unauthenticated user to /login")
    void profilePage_Unauthenticated_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}
