package com.realestate.realestate.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.realestate.dto.auth.AuthResponse;
import com.realestate.realestate.dto.auth.LoginRequest;
import com.realestate.realestate.dto.auth.RefreshTokenRequest;
import com.realestate.realestate.dto.auth.RegisterRequest;
import com.realestate.realestate.dto.user.UserResponse;
import com.realestate.realestate.entity.RefreshToken;
import com.realestate.realestate.entity.Role;
import com.realestate.realestate.entity.User;
import com.realestate.realestate.enums.RoleName;
import com.realestate.realestate.exception.DuplicateResourceException;
import com.realestate.realestate.exception.EmailNotVerifiedException;
import com.realestate.realestate.exception.ResourceNotFoundException;
import com.realestate.realestate.repository.RefreshTokenRepository;
import com.realestate.realestate.repository.RoleRepository;
import com.realestate.realestate.repository.UserRepository;
import com.realestate.realestate.security.CustomUserDetailsService;
import com.realestate.realestate.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final EmailVerificationService emailVerificationService;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        Role userRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        User user = User.builder()
                .name(request.getName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .secondLastName(request.getSecondLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .contactNumber(request.getContactNumber())
                .enabled(true)
                .emailVerified(false) 
                .roles(new HashSet<>(Set.of(userRole)))
                .build();
        
        user = userRepository.save(user);
        
        emailVerificationService.createAndSendVerificationToken(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmailWithRoles(request.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException(
                "Please verify your email before logging in. Check your inbox for the verification link."
            );
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtService.generateToken(userDetails);
        String refreshTokenValue = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        refreshTokenRepository.save(refreshToken);

        return buildAuthResponse(user, accessToken, refreshTokenValue);
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshTokenValue = request.getRefreshToken();

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token has expired");
        }

        User user = userRepository.findByEmailWithRoles(refreshToken.getUser().getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtService.generateToken(userDetails);

        return buildAuthResponse(user, accessToken, refreshTokenValue);
    }

    @Transactional
    public void logout(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        Set<RoleName> roleNames = user.getRoles() != null 
            ? user.getRoles().stream()
                    .map(role -> role.getName())
                    .collect(Collectors.toSet())
            : new HashSet<>();
        
        UserResponse userResponse = UserResponse.builder()
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

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getExpirationTime())
                .user(userResponse)
                .build();
    }
}
