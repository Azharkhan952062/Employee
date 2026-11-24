package com.ems.employeemanagement.service;

import com.ems.employeemanagement.dto.UserProfileDto;
import com.ems.employeemanagement.dto.UserRegistrationDto;
import com.ems.employeemanagement.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    User registerUser(UserRegistrationDto registrationDto);

    User findByEmail(String email);

    UserProfileDto getUserProfile(String email);
}
