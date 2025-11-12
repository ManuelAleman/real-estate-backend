package com.realestate.realestate.service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.realestate.dto.user.UserResponse;
import com.realestate.realestate.entity.User;
import com.realestate.realestate.enums.RoleName;
import com.realestate.realestate.exception.common.ResourceNotFoundException;
import com.realestate.realestate.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        log.debug("Getting current user by email: {}", email);
        
        User user = userRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return buildUserResponse(user);
    }

    private UserResponse buildUserResponse(User user) {
        Set<RoleName> roleNames = user.getRoles() != null 
            ? user.getRoles().stream()
                    .map(role -> role.getName())
                    .collect(Collectors.toSet())
            : new HashSet<>();

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .secondLastName(user.getSecondLastName())
                .email(user.getEmail())
                .contactNumber(user.getContactNumber())
                .profilePicture(user.getProfilePicture())
                .emailVerified(user.isEmailVerified())
                .roles(roleNames)
                .build();
    }
}
