package com.realestate.realestate.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.realestate.entity.EmailVerificationToken;
import com.realestate.realestate.entity.User;
import com.realestate.realestate.exception.auth.AlreadyVerifiedException;
import com.realestate.realestate.exception.common.ResourceNotFoundException;
import com.realestate.realestate.exception.auth.TokenExpiredException;
import com.realestate.realestate.repository.EmailVerificationTokenRepository;
import com.realestate.realestate.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    private static final int EXPIRATION_HOURS = 24;

    @Transactional
    public void createAndSendVerificationToken(User user) {
        tokenRepository.findByUserAndVerifiedAtIsNull(user)
                .ifPresent(tokenRepository::delete);

        String token = UUID.randomUUID().toString();

        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusHours(EXPIRATION_HOURS))
                .build();

        tokenRepository.save(verificationToken);

        emailService.sendVerificationEmail(user.getEmail(), user.getName(), token);
        
        log.info("Verification token created for user: {}", user.getEmail());
    }

    @Transactional
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid verification token. Please request a new one."));

        if (verificationToken.isVerified()) {
            throw new AlreadyVerifiedException("Your email has already been verified. You can log in now!");
        }

        if (verificationToken.isExpired()) {
            throw new TokenExpiredException("Verification token has expired. Please request a new verification email.");
        }

        verificationToken.setVerifiedAt(LocalDateTime.now());
        tokenRepository.save(verificationToken);

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        user.setEmailVerifiedAt(LocalDateTime.now());
        userRepository.save(user);

        emailService.sendWelcomeEmail(user.getEmail(), user.getName());

        log.info("Email verified for user: {}", user.getEmail());
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (user.isEmailVerified()) {
            throw new AlreadyVerifiedException("Email is already verified. You can log in.");
        }

        createAndSendVerificationToken(user);
    }
}
