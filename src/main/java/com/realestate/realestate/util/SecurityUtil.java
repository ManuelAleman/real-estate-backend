package com.realestate.realestate.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.realestate.realestate.entity.User;
import com.realestate.realestate.exception.common.ResourceNotFoundException;
import com.realestate.realestate.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityUtil {
    private static UserRepository userRepository;

    public static User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if( authentication == null || !authentication.isAuthenticated() ) {
            throw new IllegalStateException("No authenticated user found");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
