package com.realestate.realestate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.realestate.dto.auth.MessageResponse;
import com.realestate.realestate.dto.seller.SellerApplicationRequest;
import com.realestate.realestate.dto.seller.SellerStatusResponse;
import com.realestate.realestate.service.SellerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @PostMapping("/apply")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> applyToBecomeSeller(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SellerApplicationRequest request) {
        
        sellerService.applyToBecomeSeller(userDetails.getUsername(), request);
        
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Your application has been submitted successfully. We will review it soon.")
                .build());
    }

    @GetMapping("/application/status")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SellerStatusResponse> getApplicationStatus(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        SellerStatusResponse status = sellerService.getApplicationStatus(userDetails.getUsername());
        return ResponseEntity.ok(status);
    }
}
