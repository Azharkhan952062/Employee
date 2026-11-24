package com.ems.employeemanagement.service;

import com.ems.employeemanagement.dto.UserProfileDto;
import com.ems.employeemanagement.dto.UserRegistrationDto;
import com.ems.employeemanagement.entity.User;
import com.ems.employeemanagement.exception.DuplicateResourceException;
import com.ems.employeemanagement.repository.UserRepository;
import com.ems.employeemanagement.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;
    private UserRegistrationDto sampleRegistration;

    @BeforeEach
    void setUp() {
        sampleUser = new User(1L, "Admin User", "admin@ems.com", "hashed_password", "ROLE_ADMIN");
        sampleRegistration = new UserRegistrationDto("Admin User", "admin@ems.com", "Password123", "Password123");
    }

    @Test
    @DisplayName("Should successfully register user with hashed password")
    void registerUser_Success() {
        when(userRepository.existsByEmail("admin@ems.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        User registered = userService.registerUser(sampleRegistration);

        assertThat(registered).isNotNull();
        assertThat(registered.getEmail()).isEqualTo("admin@ems.com");
        verify(passwordEncoder, times(1)).encode("Password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when registering existing email")
    void registerUser_DuplicateEmail_ThrowsException() {
        when(userRepository.existsByEmail("admin@ems.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(sampleRegistration))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when passwords do not match")
    void registerUser_PasswordMismatch_ThrowsException() {
        sampleRegistration.setConfirmPassword("MismatchPassword");

        assertThatThrownBy(() -> userService.registerUser(sampleRegistration))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Passwords do not match");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should load user by username (email) for Spring Security")
    void loadUserByUsername_Success() {
        when(userRepository.findByEmail("admin@ems.com")).thenReturn(Optional.of(sampleUser));

        UserDetails userDetails = userService.loadUserByUsername("admin@ems.com");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("admin@ems.com");
        assertThat(userDetails.getPassword()).isEqualTo("hashed_password");
        assertThat(userDetails.getAuthorities()).hasSize(1);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user does not exist")
    void loadUserByUsername_NotFound_ThrowsException() {
        when(userRepository.findByEmail("unknown@ems.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("unknown@ems.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    @DisplayName("Should return user profile DTO")
    void getUserProfile_Success() {
        when(userRepository.findByEmail("admin@ems.com")).thenReturn(Optional.of(sampleUser));

        UserProfileDto profile = userService.getUserProfile("admin@ems.com");

        assertThat(profile).isNotNull();
        assertThat(profile.getFullName()).isEqualTo("Admin User");
        assertThat(profile.getRole()).isEqualTo("ADMIN");
    }
}
