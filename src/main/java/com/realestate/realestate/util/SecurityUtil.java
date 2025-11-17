package com.realestate.realestate.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.realestate.realestate.entity.Seller;
import com.realestate.realestate.entity.User;
import com.realestate.realestate.exception.common.ResourceNotFoundException;
import com.realestate.realestate.repository.SellerRepository;
import com.realestate.realestate.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityUtil {
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public Seller getCurrentSeller() {
        User currentUser = getCurrentUser();
        return sellerRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Seller profile not found for user: " + currentUser.getEmail()));
    }
}
