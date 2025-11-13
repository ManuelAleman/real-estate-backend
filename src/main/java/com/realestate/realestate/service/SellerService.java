package com.realestate.realestate.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.realestate.realestate.dto.seller.SellerApplicationRequest;
import com.realestate.realestate.dto.seller.SellerResponse;
import com.realestate.realestate.dto.seller.SellerStatusResponse;
import com.realestate.realestate.dto.seller.VerifySellerRequest;
import com.realestate.realestate.entity.Role;
import com.realestate.realestate.entity.Seller;
import com.realestate.realestate.entity.User;
import com.realestate.realestate.enums.RoleName;
import com.realestate.realestate.enums.SellerStatus;
import com.realestate.realestate.exception.common.ResourceNotFoundException;
import com.realestate.realestate.exception.seller.InvalidSellerStatusException;
import com.realestate.realestate.exception.seller.SellerAlreadyExistsException;
import com.realestate.realestate.repository.RoleRepository;
import com.realestate.realestate.repository.SellerRepository;
import com.realestate.realestate.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerService {
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public void applyToBecomeSeller(String userEmail, SellerApplicationRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        sellerRepository.findByUser(user).ifPresentOrElse(
                existingSeller -> {
                    if (existingSeller.getStatus() == SellerStatus.PENDING) {
                        throw new SellerAlreadyExistsException("You already have a pending application");
                    }
                    if (existingSeller.getStatus() == SellerStatus.APPROVED) {
                        throw new SellerAlreadyExistsException("You are already an approved seller");
                    }

                    if (existingSeller.getStatus() == SellerStatus.REJECTED) {
                        existingSeller.setCity(request.getCity());
                        existingSeller.setAddress(request.getAddress());
                        existingSeller.setCompanyName(request.getCompanyName());
                        existingSeller.setLicenseNumber(request.getLicenseNumber());
                        existingSeller.setBio(request.getBio());
                        existingSeller.setStatus(SellerStatus.PENDING);
                        existingSeller.setVerifiedAt(null);
                        existingSeller.setVerificationNotes(null);
                        sellerRepository.save(existingSeller);
                        log.info("User {} has re-applied to become a seller", userEmail);
                    }
                },
                () -> {
                    Seller seller = Seller.builder()
                            .user(user)
                            .city(request.getCity())
                            .address(request.getAddress())
                            .companyName(request.getCompanyName())
                            .licenseNumber(request.getLicenseNumber())
                            .bio(request.getBio())
                            .status(SellerStatus.PENDING)
                            .build();
                    sellerRepository.save(seller);
                    log.info("User {} has applied to become a seller", userEmail);
                });
    }

    @Transactional(readOnly = true)
    public SellerStatusResponse getApplicationStatus(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Seller seller = sellerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Seller application not found"));

        return SellerStatusResponse.builder()
                .id(seller.getId())
                .status(seller.getStatus())
                .city(seller.getCity())
                .address(seller.getAddress())
                .companyName(seller.getCompanyName())
                .licenseNumber(seller.getLicenseNumber())
                .bio(seller.getBio())
                .createdAt(seller.getCreatedAt())
                .verifiedAt(seller.getVerifiedAt())
                .verificationNotes(seller.getVerificationNotes())
                .build();
    }

    @Transactional(readOnly = true)
    public List<SellerResponse> getPendingSellers() {
        List<Seller> pendingSellers = sellerRepository.findByStatus(SellerStatus.PENDING);
        return pendingSellers.stream()
                .map(this::buildSellerResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SellerResponse getSellerById(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        return buildSellerResponse(seller);
    }

    @Transactional
    public void verifySeller(Long sellerId, VerifySellerRequest request) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        if (seller.getStatus() != SellerStatus.PENDING) {
            throw new InvalidSellerStatusException("Only pending sellers can be verified or rejected");
        }

        if (request.getApproved()) {
            seller.setStatus(SellerStatus.APPROVED);
            seller.setVerifiedAt(java.time.LocalDateTime.now());
            
            User user = seller.getUser();
            Role sellerRole = roleRepository.findByName(RoleName.SELLER)
                    .orElseThrow(() -> new ResourceNotFoundException("SELLER role not found"));
            
            user.getRoles().add(sellerRole);
            userRepository.save(user);
            
            log.info("SELLER role added to user {}", user.getEmail());
        } else {
            seller.setStatus(SellerStatus.REJECTED);
        }

        seller.setVerificationNotes(request.getNotes());
        sellerRepository.save(seller);
        log.info("Seller {} has been {}", sellerId, request.getApproved() ? "approved" : "rejected");
    }

    private SellerResponse buildSellerResponse(Seller seller) {
        User user = seller.getUser();

        return SellerResponse.builder()
                .id(seller.getId())
                .userId(user.getId())
                .userName(user.getName() + " " + user.getLastName())
                .userEmail(user.getEmail())
                .userPhone(user.getContactNumber())
                .userProfilePicture(user.getProfilePicture())
                .city(seller.getCity())
                .address(seller.getAddress())
                .companyName(seller.getCompanyName())
                .licenseNumber(seller.getLicenseNumber())
                .bio(seller.getBio())
                .status(seller.getStatus())
                .rating(seller.getRating())
                .verifiedAt(seller.getVerifiedAt())
                .createdAt(seller.getCreatedAt())
                .verificationNotes(seller.getVerificationNotes())
                .build();
    }
}
